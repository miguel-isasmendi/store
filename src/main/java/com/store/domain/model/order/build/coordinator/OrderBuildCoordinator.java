package com.store.domain.model.order.build.coordinator;

import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.store.architecture.utils.DateUtils;
import com.store.domain.model.order.Order;
import com.store.domain.model.order.OrderItem;
import com.store.domain.model.order.data.OrderCreationData;
import com.store.domain.model.order.data.OrderCreationData.OrderCreationDataBuilder;
import com.store.domain.model.order.data.OrderData;
import com.store.domain.model.order.data.OrderData.OrderDataBuilder;
import com.store.domain.model.order.data.OrderDiscountCreationData;
import com.store.domain.model.order.data.OrderItemCreationData;
import com.store.domain.model.order.data.OrderItemData;
import com.store.domain.model.order.data.OrderStatusModificationData;
import com.store.domain.model.order.dto.OrderCreationDto;
import com.store.domain.model.order.dto.OrderDiscountCreationDto;
import com.store.domain.model.order.dto.OrderDto;
import com.store.domain.model.order.dto.OrderDto.OrderDtoBuilder;
import com.store.domain.model.order.dto.OrderItemCreationDto;
import com.store.domain.model.order.dto.OrderItemDto;
import com.store.domain.model.order.dto.OrderStatusModificationDto;
import com.store.domain.model.sku.data.SkuData;
import com.store.domain.service.catalog.CatalogCoordinatorService;

public class OrderBuildCoordinator {
	public static OrderDto toDto(OrderData order, CatalogCoordinatorService coordinatorService) {

		OrderDtoBuilder builder = OrderDto.builder()
				.orderContact(OrderContactBuildCoordinator.toDto(order.getOrderContact()))
				.cancelledOn(order.getCancelledOn())
				.orderPayment(OrderPaymentBuildCoordinator.toDto(order.getPayment()))
				.delivery(OrderDeliveryBuildCoordinator.toDto(order.getDelivery())).orderId(order.getOrderId())
				.status(order.getStatus()).createdByUserId(order.getCreatedByUserId()).createdOn(order.getCreatedOn())
				.deliveryCost(order.getDeliveryCost()).subtotal(order.getSubtotal())
				.totalAmount(order.getTotalAmount());

		builder.items(order.getItems().stream().map(OrderBuildCoordinator::toDto).collect(Collectors.toList()));

		builder.discounts(
				order.getDiscounts().stream().map(OrderDiscountBuildCoordinator::toDto).collect(Collectors.toList()));

		return builder.build();
	}

	public static OrderItemDto toDto(OrderItemData orderItem) {

		return OrderItemDto.builder().quantity(orderItem.getQuantity()).skuId(orderItem.getSkuId())
				.price(orderItem.getPrice()).createdByUserId(orderItem.getCreatedByUserId())
				.createdOn(orderItem.getCreatedOn()).orderItemId(orderItem.getOrderItemId()).build();
	}

	public static OrderItemData toData(OrderItemDto orderItem) {
		return OrderItemData.builder().quantity(orderItem.getQuantity()).skuId(orderItem.getSkuId())
				.price(orderItem.getPrice()).createdByUserId(orderItem.getCreatedByUserId())
				.createdOn(orderItem.getCreatedOn()).orderItemId(orderItem.getOrderItemId()).build();
	}

	public static OrderData toData(OrderDto order) {
		OrderDataBuilder builder = OrderData.builder().deliveryCost(order.getDeliveryCost())
				.subtotal(order.getSubtotal()).totalAmount(order.getTotalAmount());

		builder.items(order.getItems().stream().map(OrderBuildCoordinator::toData).collect(Collectors.toList()));

		builder.discounts(
				order.getDiscounts().stream().map(OrderDiscountBuildCoordinator::toData).collect(Collectors.toList()));

		return builder.build();
	}

	public static OrderItemCreationData toData(OrderItemCreationDto orderItem,
			CatalogCoordinatorService catalogService) {
		SkuData sku = catalogService.getSkuById(orderItem.getSkuId());

		return OrderItemCreationData.builder().quantity(orderItem.getQuantity()).skuId(orderItem.getSkuId())
				.price(sku.getPrice()).build();
	}

	public static OrderCreationData toData(OrderCreationDto order, CatalogCoordinatorService coordinatorService) {

		OrderCreationDataBuilder builder = OrderCreationData.builder()
				.contact(OrderContactBuildCoordinator.toData(order.getContact()));

		for (OrderItemCreationDto orderItem : order.getItems()) {
			builder.item(toData(orderItem, coordinatorService));
		}

		builder.delivery(OrderDeliveryBuildCoordinator.toData(order.getDelivery()));

		if (!CollectionUtils.isEmpty(order.getDiscounts())) {
			builder.discounts(
					order.getDiscounts().stream().map(OrderBuildCoordinator::toData).collect(Collectors.toList()));
		}

		return builder.build();
	}

	private static OrderDiscountCreationData toData(OrderDiscountCreationDto discount) {
		return OrderDiscountCreationData.builder().amount(discount.getAmount()).build();
	}

	public static OrderData toData(Order order) {
		OrderDataBuilder builder = OrderData.builder();

		builder.orderContact(OrderContactBuildCoordinator.toData(order.getOrderContact()))
				.delivery(OrderDeliveryBuildCoordinator.toData(order.getDelivery())).orderId(order.getOrderId())
				.status(order.getStatus()).createdByUserId(order.getCreatedByUserId())
				.createdOn(DateUtils.dateFrom(order.getCreatedOn())).deliveryCost(order.getDeliveryCost())
				.totalDiscount(order.getTotalDiscount()).subtotal(order.getSubtotal())
				.totalAmount(order.getTotalAmount());

		if (order.getCancelledOn() != null) {
			builder.cancelledOn(DateUtils.dateFrom(order.getCancelledOn()));
		}

		builder.items(order.getItems().stream().map(OrderBuildCoordinator::toData).collect(Collectors.toList()));

		builder.discounts(
				order.getDiscounts().stream().map(OrderDiscountBuildCoordinator::toData).collect(Collectors.toList()));

		builder.payment(OrderPaymentBuildCoordinator.toData(order.getPayment()));

		return builder.build();
	}

	public static OrderItemData toData(OrderItem orderItem) {
		return OrderItemData.builder().quantity(orderItem.getQuantity()).skuId(orderItem.getSkuId())
				.price(orderItem.getPrice()).createdByUserId(orderItem.getCreatedByUserId())
				.createdOn(DateUtils.dateFrom(orderItem.getCreatedOn())).orderItemId(orderItem.getOrderItemId())
				.build();
	}

	public static OrderStatusModificationData toData(OrderStatusModificationDto orderStatusModificationDto) {
		return OrderStatusModificationData.builder().status(orderStatusModificationDto.getStatus()).build();
	}

}
