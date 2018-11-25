package com.tan.order.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.common.IdWorker;
import com.tan.dao.OrderItemMapper;
import com.tan.dao.OrderMapper;
import com.tan.dao.PayLogMapper;
import com.tan.pojo.TbOrder;
import com.tan.pojo.TbOrderItem;
import com.tan.pojo.TbPayLog;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.Cart;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    //Redis中购物车数据的key
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;
    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 生成订单，并保存到Redis5分钟(未实现)
     * @param order
     * @return
     */
    @Override
    public String addOrder(TbOrder order) {
        String outTradeNo = "";
        List<Cart> cart = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());
        if(cart.size() >0 && cart != null){
            double totalFee = 0.0;// 本次应该支付总金额
            String orderIds = "";// 本次交易的订单 id 集合
            for (Cart cart1 : cart) {
                long orderId =idWorker.nextId();
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(orderId);
                tbOrder.setSourceType(order.getSourceType());
                tbOrder.setUserId(order.getUserId());
                tbOrder.setStatus("1");//未付款
                tbOrder.setPaymentType(order.getPaymentType());// 支付类型
                tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货人地址
                tbOrder.setReceiver(order.getReceiver());//收货人
                tbOrder.setCreateTime(new Date());//创建时间
                tbOrder.setUpdateTime(tbOrder.getCreateTime());//修改时间
                tbOrder.setSellerId(cart1.getSellerId());//卖家
                //本笔订单的明细
                double payment = 0.0;
                for (TbOrderItem tbOrderItem : cart1.getOrderItemList()) {
                     tbOrderItem.setId(idWorker.nextId());
                     tbOrderItem.setOrderId(orderId);
                     payment += tbOrderItem.getTotalFee().doubleValue();
                     //新增订单到Mysql
                     orderItemMapper.insertSelective(tbOrderItem);
                }
                tbOrder.setPayment(new BigDecimal(payment));
                orderMapper.insertSelective(tbOrder);
                //记录订单Id;
                if(orderIds.length() > 0 ){
                    orderIds += "," + orderId;
                }else{
                    orderIds = orderId+"";
                }
                totalFee += payment;




            }
            //如果是1那么就是微信支付即需要生成支付日志保存到Mysql和Redis中设置5分钟
            if("1".equals(order.getPaymentType())){
                outTradeNo = idWorker.nextId()+"";
                TbPayLog tbPayLog = new TbPayLog();
                tbPayLog.setCreateTime(new Date());
                tbPayLog.setOutTradeNo(outTradeNo);
                tbPayLog.setTradeState("0");//未支付
                tbPayLog.setTotalFee((long)(totalFee*100));//总金额
                tbPayLog.setUserId(order.getUserId());//买家
                tbPayLog.setOrderList(orderIds);//本次订单集合(即买了什么)
                //新增支付日志
                payLogMapper.insertSelective(tbPayLog);
            }
            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());
        }

        return outTradeNo;
    }

    /**
     * 查询订单日志表
     * @param outTradeNo 当前订单
     * @return 订单日志表对象
     */
    @Override
    public TbPayLog findPayLogByOutTradeNo(String outTradeNo) {
        return payLogMapper.selectByPrimaryKey(outTradeNo);
    }

    /**
     * 扫描二维码成功时修改订单日志表、订单表的支付状态
     * @param outTradeNo
     * @param transaction_id
     */
    @Override
    public void updateOrderStatus(String outTradeNo, String transaction_id) {
        //修改订单日志的支付状态
        TbPayLog tbPayLog = findPayLogByOutTradeNo(outTradeNo);
        tbPayLog.setTradeState("1");//已支付
        tbPayLog.setCreateTime(new Date());
        tbPayLog.setTransactionId(transaction_id);
        payLogMapper.updateByPrimaryKeySelective(tbPayLog);

        //修改TbOrder表的支付状态
        String[] orderList = tbPayLog.getOrderList().split(",");
        TbOrder tbOrder = new TbOrder();
        tbOrder.setPaymentTime(new Date());
        tbOrder.setStatus("2");

        Example example = new Example(TbOrder.class);
        example.createCriteria().andIn("orderId", Arrays.asList(orderList));
        orderMapper.updateByExampleSelective(tbOrder,example);
    }
}
