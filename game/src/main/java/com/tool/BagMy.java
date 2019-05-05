package com.tool;

import com.entry.BagEntry;
import com.entry.po.ItemInfo;
import com.entry.po.ItemPo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.template.TemplateManager;
import com.template.templates.ItemTemplate;
import com.template.templates.type.OverBagType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 规则定为，能放就尽量放，放不下的发邮件
 */
@Component
public class BagMy {
    @Autowired
    private TemplateManager tm;
    public int maxIndex = 64;
    public Map<Long, Map<Integer, ItemPo>> idIndexMap = new HashMap<>();
    //    public Map<Long, Integer> numMap = new HashMap<>();
    public Map<Integer, ItemPo> indexMap = new HashMap<>();
    public List<Integer> emptyList = new ArrayList<>();


    public void initWithBorn() {
        for (int i = 1; i < maxIndex + 1; i++) {
            indexMap.put(i, null);
        }
        calCell();
    }

    public void init(BagEntry bagEntry) {
        indexMap = bagEntry.indexMap;
        calCell();
    }


    private void addIdIndexMap(int index, ItemPo itemPo) {
        if (idIndexMap.containsKey(itemPo.id)) {
            idIndexMap.get(itemPo.id).put(index, itemPo);
        } else {
            Map<Integer, ItemPo> cellMap = Maps.newHashMap();
            cellMap.put(index, itemPo);
            idIndexMap.put(itemPo.id, cellMap);
        }
    }

    private void calCell() {
        for (Map.Entry<Integer, ItemPo> entry : indexMap.entrySet()) {
            int index = entry.getKey();
            ItemPo itemPo = entry.getValue();
            if (Objects.isNull(itemPo)) {
                emptyList.add(index);
            } else {
                addIdIndexMap(index, itemPo);
            }
        }
    }


    /**
     * 查找某一ID的空闲格集合 TODO 明天改出来一个，可以计算多物品，混合个数，测试能不能放进去的逻辑
     */
    public List<TempCell> calIdleCells(long itemId) {
        return indexMap.entrySet().stream().filter(x -> {

            ItemPo itemPo = x.getValue();

            if (Objects.isNull(itemPo)) {
                return true;
            }
            return !itemPo.isSingleNumMax();

        }).map(x -> {
            ItemTemplate t = tm.getTemplate(ItemTemplate.class, itemId);
            return new TempCell(itemId, x.getKey(), t.getSinglePlusMax() - x.getValue().num);
        }).collect(Collectors.toList());
    }

    public void realAdd(TempCell idleCell, int num) {
        indexMap.get(idleCell.tempIndex).num += num;
        idleCell.tempNum -= num;

    }

    public void addItem(ItemInfo item) {
        List<TempCell> idleCells = calIdleCells(item.id);
        if (CollectionUtils.isEmpty(idleCells)) {
            //TODO 发邮件
            return;
        }
        for (TempCell idleCell : idleCells) {
            if (item.num <= idleCell.tempNum) {
                realAdd(idleCell, item.num);
                return;
            }
            if (idleCell.tempNum < 1) {
                continue;
            }
            item.num = item.num - idleCell.tempNum;
            realAdd(idleCell, idleCell.tempNum);
        }
        if (item.num > 0) {
            //TODO 发邮件
        }

    }

    public void addItem(List<ItemInfo> itemInfos) {
        itemInfos.forEach(this::addItem);
    }

    public void addItem(Map<Long, Integer> map) {
        addItem(map.entrySet().stream()
                .map(x -> new ItemInfo(x.getKey(), x.getValue()))
                .collect(Collectors.toList()));
    }


    public void addItem(Long itemId, Integer num) {
        addItem(Lists.newArrayList(new ItemInfo(itemId, num)));
    }

    public void addItem(ItemInfo... itemInfos) {
        addItem(Arrays.asList(itemInfos));

    }
	//========================================================================
	
	public boolean putItems(List<TempCell> list)
	{
		if(Objects.isNull(list))
		{
			return false;
		}
        for (TempCell tempCell : list) {
            //TODO 创建物品的方法需要完善
            ItemPo itemPo = new ItemPo();
            itemPo.id = tempCell.tempItemId;
            itemPo.num = tempCell.tempNum;
            itemPo.index = tempCell.tempIndex;
			if(itemPo.index>0)
			{
				indexMap.put(itemPo.index,itemPo);
				//减一个空格
				emptyList.remove(itemPo.index);
				//装填map
				addIdIndexMap(itemPo.index,itemPo);
			}
			else
			{
				// 这里发邮件 ，可以定义在item表里，发不进去放在哪，也可以把isForceAdd定义为枚举，指定发不进去放哪
				switch(itemPo.index)
				{
					case OverBagType.Mail:
						//TODO 发邮件
						break;
					case OverBagType.Discard:
						//直接就不处理，忽略
						break;
					default:
						break;
				}
			}
        }
		return true;
    }
	
	
	/**
     * 把能不能放进去，转化为，一共要占用几个空格问题。
	 * 强制填加是进不去的进邮件
	 * @param itemInfos    要添加的物品s
	 * @param overBagType 放不下怎么办
	 * @return 格子记录
     */
	public List<TempCell> testPut(List<ItemInfo> itemInfos,int overBagType)
	{


        List<TempCell> tempCells = new ArrayList<>();
        //计算不用占格的
        Iterator<ItemInfo> it = itemInfos.iterator();
        int allUseNum = 0;
        while (it.hasNext()) {
            ItemInfo itemInfo = it.next();
            ItemTemplate t = tm.getTemplate(ItemTemplate.class, itemInfo.id);
            Map<Integer, ItemPo> indexItemPo = idIndexMap.get(itemInfo.getId());
            for (Map.Entry<Integer, ItemPo> entry : indexItemPo.entrySet()) {
                if (entry.getValue().isSingleNumMax()) {
                    continue;
                }
                if (itemInfo.num == 0) {
                    it.remove();
                    break;
                }
                int canPutNum = calCanPutNum(t, itemInfo, entry.getValue().num);
                TempCell tempCell = new TempCell(entry.getKey(), entry.getValue().index, canPutNum);
                tempCells.add(tempCell);

            }

            if (itemInfo.num == 0) {
                it.remove();
                continue;
            }
            //计算占几个空格
            int needEmptyNum = ((itemInfo.num - 1) / t.getSinglePlusMax()) + 1;
            allUseNum += needEmptyNum;
        }
		
		if(allUseNum>emptyList.size() && overBagType==OverBagType.Refuse)
		{
            return null;
        }
		
		
		//把空格填起来，返回记录操作数据
        List<Integer> tempEmptyList = new ArrayList<>(emptyList);
        for (ItemInfo itemInfo : itemInfos) {
            ItemTemplate t = tm.getTemplate(ItemTemplate.class, itemInfo.id);
            while (itemInfo.num > 0) {
				int index=overBagType;
				if(tempEmptyList.size()>0)
				{
					index=tempEmptyList.remove(0);
				}
                int canPutNum = calCanPutNum(t, itemInfo, 0);
                TempCell tempCell = new TempCell(itemInfo.id, index, canPutNum);
                tempCells.add(tempCell);
            }
        }
        return tempCells;

    }

    private int calCanPutNum(ItemTemplate t, ItemInfo itemInfo, int hasNum) {
        int canPutNum = t.getSinglePlusMax() - hasNum;
        if (canPutNum >= itemInfo.num) {
            itemInfo.num = 0;
            return canPutNum;
        } else {
            itemInfo.num -= canPutNum;
            return canPutNum;
        }
    }

    @Data
    @AllArgsConstructor
    public static class TempCell {
        private long tempItemId;
        private int tempIndex;
        private int tempNum;
    }

}
