package com.tool;

import com.entry.BagEntry;
import com.entry.po.ItemInfo;
import com.entry.po.ItemPo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.template.TemplateManager;
import com.template.templates.ItemTemplate;
import com.template.templates.type.OverBagType;
import com.util.Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 背包
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
            calIndexCell(entry.getKey(), entry.getValue());
        }
    }

    private void calIndexCell(int index, ItemPo itemPo) {
        if (Objects.isNull(itemPo)) {
            emptyList.add(index);
        } else {
            addIdIndexMap(index, itemPo);
        }
    }


    public boolean putItems(List<TempCell> list) {
        if (Objects.isNull(list)) {
            return false;
        }
        for (TempCell tempCell : list) {
            //TODO 创建物品的方法需要完善
            ItemPo itemPo = new ItemPo();
            itemPo.id = tempCell.tempItemId;
            itemPo.num = tempCell.tempNum;
            itemPo.index = tempCell.tempIndex;
            if (itemPo.index > 0) {
                indexMap.put(itemPo.index, itemPo);
                //减一个空格
                emptyList.remove(itemPo.index);
                //装填map
                addIdIndexMap(itemPo.index, itemPo);
            } else {
                // 这里发邮件 ，可以定义在item表里，发不进去放在哪，也可以把isForceAdd定义为枚举，指定发不进去放哪
                switch (itemPo.index) {
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
     *
     * @param itemInfos   要添加的物品s
     * @param overBagType 放不下怎么办
     * @return 格子记录
     */
    public List<TempCell> testPut(List<ItemInfo> itemInfos, int overBagType) {


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

        if (allUseNum > emptyList.size() && overBagType == OverBagType.Refuse) {
            return null;
        }


        //把空格填起来，返回记录操作数据
        List<Integer> tempEmptyList = new ArrayList<>(emptyList);
        for (ItemInfo itemInfo : itemInfos) {
            ItemTemplate t = tm.getTemplate(ItemTemplate.class, itemInfo.id);
            while (itemInfo.num > 0) {
                int index = overBagType;
                if (tempEmptyList.size() > 0) {
                    index = tempEmptyList.remove(0);
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
    
    
    private int calCanGetNum(ItemInfo itemInfo, int hasNum) {
        int canGetNum = hasNum;
        if (canGetNum >= itemInfo.num) {
            canGetNum -= itemInfo.num;
            itemInfo.num =0;
            return canGetNum;
        } else {
            itemInfo.num -= canGetNum;
            return canGetNum;
        }
    }
    /**
     * 添加物品，尽量放，放不下的发邮件
     *
     * @param itemInfos 物品s
     */
    public void addItemMail(List<ItemInfo> itemInfos) {
        List<TempCell> tempCells = testPut(itemInfos, OverBagType.Mail);
        putItems(tempCells);
    }

    public void addItemMail(Map<Long, Integer> map) {
        addItemMail(map.entrySet().stream()
                .map(x -> new ItemInfo(x.getKey(), x.getValue()))
                .collect(Collectors.toList()));
    }


    public void addItemMail(Long itemId, Integer num) {
        addItemMail(Lists.newArrayList(new ItemInfo(itemId, num)));
    }

    public void addItemMail(ItemInfo... itemInfos) {
        addItemMail(Arrays.asList(itemInfos));
    }

    /**
     * 添加物品，尽量放，放不下的直接丢弃
     *
     * @param itemInfos 物品s
     */
    public void addItemDiscard(List<ItemInfo> itemInfos) {
        List<TempCell> tempCells = testPut(itemInfos, OverBagType.Discard);
        putItems(tempCells);
    }

    public void addItemDiscard(Map<Long, Integer> map) {
        addItemDiscard(map.entrySet().stream()
                .map(x -> new ItemInfo(x.getKey(), x.getValue()))
                .collect(Collectors.toList()));
    }


    public void addItemDiscard(Long itemId, Integer num) {
        addItemDiscard(Lists.newArrayList(new ItemInfo(itemId, num)));
    }

    public void addItemDiscard(ItemInfo... itemInfos) {
        addItemDiscard(Arrays.asList(itemInfos));
    }

    /**
     * 添加物品，放不下返回false
     *
     * @param itemInfos 物品s
     * @return 放不下返回false
     */
    public boolean addItemRefuse(List<ItemInfo> itemInfos) {
        List<TempCell> tempCells = testPut(itemInfos, OverBagType.Refuse);
        return putItems(tempCells);
    }

    public boolean addItemRefuse(Map<Long, Integer> map) {
        return addItemRefuse(map.entrySet().stream()
                .map(x -> new ItemInfo(x.getKey(), x.getValue()))
                .collect(Collectors.toList()));
    }


    public boolean addItemRefuse(Long itemId, Integer num) {
        return addItemRefuse(Lists.newArrayList(new ItemInfo(itemId, num)));
    }

    public boolean addItemRefuse(ItemInfo... itemInfos) {
        return addItemRefuse(Arrays.asList(itemInfos));

    }


    /**
     * 整理
     * 由于，放入是紧着没满的放，用是紧着没满的用，所以整理只需要做排序就可以了
     */
    public void tidy() {
        LinkedHashMap<Integer, ItemPo> sortedMap = Util.mapValueSort(indexMap);
        indexMap.clear();
        idIndexMap.clear();
        emptyList.clear();
        Iterator<ItemPo> it = sortedMap.values().iterator();
        int index = 0;
        while (it.hasNext()) {
            ItemPo next = it.next();
            index++;
            next.setIndex(index);
            indexMap.put(index, next);
            calIndexCell(index, next);
        }
    }

    
    private void setCellEmpty(int index){
        ItemPo itemPo=indexMap.get(index);
        if(Objects.isNull(itemPo))
        {
            return;
        }
        indexMap.put(index,null);
        idIndexMap.get(itemPo.id).remove(index);
        emptyList.add(index);
    }
    
    public boolean costItems(List<ItemInfo> list)
    {
        List<TempCell> tempCells=testGet(list);
        if(Objects.isNull(tempCells))
        {
            return false;
        }
        for(TempCell tempCell : tempCells)
        {
            ItemPo itemPo=indexMap.get(tempCell.tempIndex);
            itemPo.num-=tempCell.getTempNum();
            if(itemPo.num==0)
            {
                setCellEmpty(itemPo.index);
            }
        }
        return true;
    }
    
    public List<TempCell> testGet(List<ItemInfo> list){
        List<TempCell> tempCells = new ArrayList<>();
    
        for (ItemInfo itemInfo : list) {
            //先判断一遍够不够
           if( idIndexMap.get(itemInfo.id).values().stream().mapToInt(ItemPo::getNum).sum()<itemInfo.num){
               return null;
           }
        }
    
        for(ItemInfo itemInfo : list)
        {
            Map<Integer,ItemPo> integerItemPoMap=idIndexMap.get(itemInfo.id);
            List<ItemPo> itemPos=Util.mapValueSortReturnList(integerItemPoMap);
        
        
            for(ItemPo itemPo : itemPos)
            {
                int canGetNum=calCanGetNum(itemInfo,itemPo.num);
                TempCell tempCell=new TempCell(itemInfo.id,itemPo.index,canGetNum);
                tempCells.add(tempCell);
                if(itemInfo.num<=0)
                {
                    break;
                }
            }
        }
        return tempCells;

    }
    
    public boolean costItems(Map<Long, Integer> map) {
        return costItems(map.entrySet().stream()
                .map(x -> new ItemInfo(x.getKey(), x.getValue()))
                .collect(Collectors.toList()));
    }
    
    
    public boolean costItems(Long itemId, Integer num) {
        return costItems(Lists.newArrayList(new ItemInfo(itemId, num)));
    }
    
    public boolean costItems(ItemInfo... itemInfos) {
        return addItemRefuse(Arrays.asList(itemInfos));
        
    }


    @Data
    @AllArgsConstructor
    public static class TempCell {
        private long tempItemId;
        private int tempIndex;
        private int tempNum;
    }

}
