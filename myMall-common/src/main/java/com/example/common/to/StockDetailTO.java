package com.example.common.to;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 库存工作单
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:10:48
 */
@Data
public class StockDetailTO {

	/**
	 * id
	 */
	private Long id;
	/**
	 * sku_id
	 */
	private Long skuId;
	/**
	 * sku_name
	 */
	private String skuName;
	/**
	 * 购买个数
	 */
	private Integer skuNum;
	/**
	 * 工作单id
	 */
	private Long taskId;
	/**
	 * 仓库Id
	 */
	private Long wareId;
	/**
	 * 锁定状态
	 * 1-锁定,2-解锁,3-已经扣减
	 */
	private Integer lockStatus;

}
