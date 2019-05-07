package com.abs.impl;

import com.abs.NoCellBagAbs;
import com.template.TemplateManager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 无格背包实现，可做货币，数值化东西，也可做无格背包
 */

@Slf4j
public  class CommonNoCellBagAbs extends NoCellBagAbs
{

    private TemplateManager tm;
    public Map<Integer,Long> map=new HashMap<>();
    
    
    public void init(Map<Integer,Long> map,TemplateManager templateManager)
    {
        this.tm=templateManager;
        this.map=map;
        
    }



}
