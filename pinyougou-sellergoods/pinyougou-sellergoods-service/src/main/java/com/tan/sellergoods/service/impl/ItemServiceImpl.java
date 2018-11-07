package com.tan.sellergoods.service.impl;

import com.tan.pojo.TbItem;
import com.tan.sellergoods.service.ItemService;
import com.tan.service.impl.BaseServiceImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ItemServiceImpl extends BaseServiceImpl<TbItem> implements ItemService<TbItem>  {

}
