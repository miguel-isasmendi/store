package com.store.domain.dao.order.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.FullEntity.Builder;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;
import com.google.inject.Inject;
import com.store.architecture.exception.dao.InvalidArgumentsDaoException;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.utils.DateUtils;
import com.store.domain.dao.DaoConstants;
import com.store.domain.dao.order.OrderDao;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.order.Order;
import com.store.domain.model.order.Order.OrderBuilder;
import com.store.domain.model.order.OrderContact;
import com.store.domain.model.order.OrderDelivery;
import com.store.domain.model.order.OrderDelivery.OrderDeliveryBuilder;
import com.store.domain.model.order.OrderDeliveryStatus;
import com.store.domain.model.order.OrderDiscount;
import com.store.domain.model.order.OrderItem;
import com.store.domain.model.order.OrderPayment;
import com.store.domain.model.order.OrderPayment.OrderPaymentBuilder;
import com.store.domain.model.order.OrderPaymentItem;
import com.store.domain.model.order.OrderPaymentStatus;
import com.store.domain.model.order.OrderStatus;
import com.store.domain.model.order.data.OrderCreationData;
import com.store.domain.model.order.data.OrderDeliveryStatusModificationData;
import com.store.domain.model.order.data.OrderDiscountCreationData;
import com.store.domain.model.order.data.OrderPaymentItemCreationData;
import com.store.domain.model.order.data.OrderStatusModificationData;

import lombok.NonNull;

public class DatastoreOrderDao implements OrderDao {

	public static final String ORDER_KIND = "Order";

	private static final String ORDER_SUBTOTAL = "subtotal";
	private static final String ORDER_DELIVERY_COST = "deliveryCost";
	private static final String ORDER_DISCOUNT_COST = "discountCost";
	private static final String ORDER_TOTAL_AMOUNT = "totalAmount";

	private static final String ITEM_KIND = "OrderItem";
	private static final String ITEM_SKU_ID = "skuId";
	private static final String ORDER_ID = "orderId";

	public static final String DELIVERY_KIND = "OrderDelivery";
	private static final String DELIVERY_DUE_DATE = "dueDate";

	public static final String ORDER_DELIVERY_ADDRESS_KIND = "OrderDeliveryAddress";
	private static final String ORDER_DELIVERY_ID = "orderDeliveryId";

	public static final String ORDER_CLIENT_CONTACT_KIND = "OrderClientContact";
	private static final String ORDER_CLIENT_CONTACT_CLIENT_ID = "clientId";

	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";

	public static String ORDER_PAYMENT_KIND = "OrderPayment";

	private static String ORDER_PAYMENT_ID = "orderPaymentId";
	private static String ORDER_PAYMENT_TOTAL_AMOUNT = "totalAmount";

	public static String ORDER_PAYMENT_ITEM_KIND = "OrderPaymentItem";

	public static String ORDER_DISCOUNT_KIND = "OrderDiscount";

	private Datastore datastore;

	@Inject
	public DatastoreOrderDao(Datastore datastore) {
		super();
		this.datastore = datastore;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Order create(@NonNull Long userId, @NonNull OrderCreationData orderData) {
		Transaction txn = datastore.newTransaction();

		try {
			// TODO Since we have at least one item, I will take the currency from there,
			// but we have to investigate a better approach on this
			Double deliveryCost = orderData.getDelivery().getAmount();
			Double discountCost = 0d;
			Double orderSubtotal = 0d;

			orderSubtotal = orderData.getItems().stream().map(item -> item.getQuantity() * item.getPrice()).reduce(0d,
					Double::sum);

			discountCost = orderData.getDiscounts().stream().map(OrderDiscountCreationData::getAmount).reduce(0d,
					Double::sum);

			Double totalAmount = (deliveryCost + orderSubtotal) - discountCost;

			if (discountCost > 0 && totalAmount < 1) {
				throw new InvalidArgumentsDaoException(ErrorConstants.AMOUNT_OF_DISCOUNTS_EXCEEDS_ORDER_TOTAL);
			}

			FullEntity<IncompleteKey> entity = Entity.newBuilder()
					.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(ORDER_KIND).newKey()))
					.set(DaoConstants.STATUS, OrderStatus.NEW.toString()).set(ORDER_SUBTOTAL, orderSubtotal)
					.set(ORDER_TOTAL_AMOUNT, totalAmount).set(ORDER_DELIVERY_COST, deliveryCost)
					.set(ORDER_DISCOUNT_COST, discountCost).setNull(DaoConstants.CANCELLED_ON)
					.set(DaoConstants.CREATED_BY_USER_ID, userId).set(DaoConstants.CREATED_ON, Timestamp.now()).build();

			Entity order = datastore.put(entity);

			// Saving items
			List<OrderItem> items = orderData.getItems().stream()
					.map(item -> Entity.newBuilder()
							.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(ITEM_KIND).newKey()))
							.set(ORDER_ID, order.getKey().getId()).set(DaoConstants.QUANTITY, item.getQuantity())
							.set(DaoConstants.PRICE, item.getPrice()).set(ITEM_SKU_ID, item.getSkuId())
							.set(DaoConstants.CREATED_BY_USER_ID, userId).set(DaoConstants.CREATED_ON, Timestamp.now())
							.build())
					.map(datastore::put).map(this::hidrateItemFromEntity).collect(Collectors.toList());

			entity = Entity.newBuilder()
					.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(DELIVERY_KIND).newKey()))
					.set(ORDER_ID, order.getKey().getId()).set(DaoConstants.AMOUNT, deliveryCost)
					.set(DELIVERY_DUE_DATE, DateUtils.timestampFrom(orderData.getDelivery().getDueDate()))
					.set(DaoConstants.STATUS, OrderDeliveryStatus.READY_FOR_IN_STORE_PICK_UP.toString())
					.set(DaoConstants.CREATED_BY_USER_ID, userId).set(DaoConstants.CREATED_ON, Timestamp.now()).build();

			Entity delivery = datastore.put(entity);

			// Saving address
			FullEntity<IncompleteKey> deliveryAddressEntity = Entity.newBuilder()
					.setKey(datastore
							.allocateId(datastore.newKeyFactory().setKind(ORDER_DELIVERY_ADDRESS_KIND).newKey()))
					.set(DaoConstants.CREATED_BY_USER_ID, userId).set(DaoConstants.CREATED_ON, Timestamp.now())
					.set(ORDER_DELIVERY_ID, delivery.getKey().getId()).build();

			// Saving client contact
			Builder<IncompleteKey> clientContactEntityBuilder = Entity.newBuilder()
					.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(ORDER_CLIENT_CONTACT_KIND).newKey()))
					.set(ORDER_ID, order.getKey().getId()).set(DaoConstants.CREATED_BY_USER_ID, userId)
					.set(DaoConstants.CREATED_ON, Timestamp.now())
					.set(ORDER_CLIENT_CONTACT_CLIENT_ID, orderData.getContact().getClientId())
					.set(FIRST_NAME, orderData.getContact().getFirstName())
					.set(LAST_NAME, orderData.getContact().getLastName());

			clientContactEntityBuilder.set(DaoConstants.EMAIL,
					StringUtils.trimToEmpty(orderData.getContact().getEmail()));

			Entity clientContactEntity = datastore.put(clientContactEntityBuilder.build());

			// Saving payment Object
			FullEntity<IncompleteKey> paymentEntity = Entity.newBuilder()
					.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(ORDER_PAYMENT_KIND).newKey()))
					.set(DaoConstants.CREATED_BY_USER_ID, userId)
					.set(DaoConstants.STATUS, OrderPaymentStatus.NEW.toString())
					.set(DaoConstants.CREATED_ON, Timestamp.now()).set(ORDER_ID, order.getKey().getId())
					.set(ORDER_PAYMENT_TOTAL_AMOUNT, 0d).build();

			Entity deliveryAddress = datastore.put(deliveryAddressEntity);
			Entity orderPayment = datastore.put(paymentEntity);

			// Saving discounts
			List<Entity> discounts = orderData.getDiscounts().stream().map(discount -> Entity.newBuilder()
					.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(ORDER_DISCOUNT_KIND).newKey()))
					.set(ORDER_ID, order.getKey().getId()).set(DaoConstants.AMOUNT, discount.getAmount())
					.set(DaoConstants.CREATED_BY_USER_ID, userId).set(DaoConstants.CREATED_ON, Timestamp.now()).build())
					.map(datastore::put).collect(Collectors.toList());

			// Saving calculated fields for order

			Entity updatedOrderEntity = Entity.newBuilder(order).set(ORDER_SUBTOTAL, orderSubtotal)
					.set(ORDER_TOTAL_AMOUNT, totalAmount).set(ORDER_DELIVERY_COST, deliveryCost)
					.set(ORDER_DISCOUNT_COST, discountCost).build();

			datastore.update(updatedOrderEntity);

			txn.commit();

			return hidrateOrderFromEntity(updatedOrderEntity,
					hidrateOrderPaymentFromEntity(orderPayment, Collections.EMPTY_LIST.iterator()), clientContactEntity,
					delivery, deliveryAddress, items, discounts.iterator());

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	private Order hidrateHeaderOnlyOrderFromEntity(Entity orderEntity) {
		return hidrateOrderFromEntity(orderEntity, null, null, null, null, new ArrayList<OrderItem>(),
				new ArrayList<Entity>().iterator());
	}

	private OrderPayment hidrateHeaderOnlyOrderPaymentFromEntity(Entity orderPaymentEntity) {
		return hidrateOrderPaymentFromEntity(orderPaymentEntity, new ArrayList<Entity>().iterator());
	}

	private OrderPayment hidrateOrderPaymentFromEntity(Entity orderPaymentEntity,
			Iterator<Entity> orderPaymentItemEntities) {

		OrderPaymentBuilder orderPaymentBuilder = OrderPayment.builder()
				.orderPaymentId(orderPaymentEntity.getKey().getId())
				.createdByUserId(orderPaymentEntity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.createdOn(orderPaymentEntity.getTimestamp(DaoConstants.CREATED_ON))
				.totalAmount(orderPaymentEntity.getDouble(ORDER_PAYMENT_TOTAL_AMOUNT))
				.status(OrderPaymentStatus.valueOf(orderPaymentEntity.getString(DaoConstants.STATUS)))
				.orderId(orderPaymentEntity.getLong(ORDER_ID));

		Iterator<Entity> paymentItems = orderPaymentItemEntities;

		if (paymentItems == null) {
			paymentItems = getOrderPaymentItemsEntities(orderPaymentEntity.getKey().getId());
		}

		if (!paymentItems.hasNext()) {
			orderPaymentBuilder.items(new ArrayList<OrderPaymentItem>(0));
		}

		while (paymentItems.hasNext()) {
			orderPaymentBuilder.item(hidrateOrderPaymentItemFromEntity(paymentItems.next()));
		}

		return orderPaymentBuilder.build();
	}

	private QueryResults<Entity> getOrderPaymentItemsEntities(Long orderPaymentId) {
		return datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_PAYMENT_ITEM_KIND)
				.setFilter(CompositeFilter.and(PropertyFilter.eq(ORDER_PAYMENT_ID, orderPaymentId),
						PropertyFilter.isNull(DaoConstants.INVALID_FROM)))
				.build());
	}

	private OrderPaymentItem hidrateOrderPaymentItemFromEntity(Entity orderPaymentItemEntity) {
		return OrderPaymentItem.builder().orderPaymentItemId(orderPaymentItemEntity.getKey().getId())
				.createdByUserId(orderPaymentItemEntity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.createdOn(orderPaymentItemEntity.getTimestamp(DaoConstants.CREATED_ON))
				.date(orderPaymentItemEntity.getTimestamp(DaoConstants.DATE))
				.orderPaymentId(orderPaymentItemEntity.getLong(ORDER_PAYMENT_ID))
				.amount(orderPaymentItemEntity.getDouble(DaoConstants.AMOUNT)).build();
	}

	private Order hidrateOrderFromEntity(Entity entity, OrderPayment orderPaymentSaved, Entity contactEntity,
			Entity orderDeliveryEntity, Entity orderDeliveryAddress, List<OrderItem> items,
			Iterator<Entity> discounts) {
		OrderBuilder orderBuilder = Order.builder().createdOn(entity.getTimestamp(DaoConstants.CREATED_ON))
				.cancelledOn((Timestamp) entity.getValue(DaoConstants.CANCELLED_ON).get())
				.createdByUserId(entity.getLong(DaoConstants.CREATED_BY_USER_ID)).orderId(entity.getKey().getId())
				.status(OrderStatus.valueOf(entity.getString(DaoConstants.STATUS)))
				.totalAmount(entity.getDouble(ORDER_TOTAL_AMOUNT)).deliveryCost(entity.getDouble(ORDER_DELIVERY_COST))
				.totalDiscount(entity.getDouble(ORDER_DISCOUNT_COST)).subtotal(entity.getDouble(ORDER_SUBTOTAL));

		Entity clientContactEntity = contactEntity;

		if (clientContactEntity == null) {
			clientContactEntity = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_CLIENT_CONTACT_KIND)
					.setFilter(PropertyFilter.eq(ORDER_ID, entity.getKey().getId())).build()).next();
		}

		orderBuilder.orderContact(OrderContact.builder().orderId(clientContactEntity.getLong(ORDER_ID))
				.orderContactId(clientContactEntity.getKey().getId())
				.clientId(clientContactEntity.getLong(ORDER_CLIENT_CONTACT_CLIENT_ID))
				.email(clientContactEntity.getString(DaoConstants.EMAIL))
				.firstName(clientContactEntity.getString(FIRST_NAME)).lastName(clientContactEntity.getString(LAST_NAME))
				.createdByUserId(clientContactEntity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.createdOn(clientContactEntity.getTimestamp(DaoConstants.CREATED_ON)).build());

		if (items == null) {
			Query<Entity> itemQuery = Query.newEntityQueryBuilder().setKind(ITEM_KIND)
					.setFilter(PropertyFilter.eq(ORDER_ID, entity.getKey().getId())).build();

			orderBuilder.items(hidrateItemList(datastore.run(itemQuery)));
		} else {
			orderBuilder.items(items);
		}

		Entity deliveryEntity = orderDeliveryEntity;
		Entity deliveryAddress = orderDeliveryAddress;

		if (deliveryEntity == null) {
			deliveryEntity = datastore.run(Query.newEntityQueryBuilder().setKind(DELIVERY_KIND)
					.setFilter(PropertyFilter.eq(ORDER_ID, entity.getKey().getId())).build()).next();
		}

		if (deliveryAddress == null) {
			deliveryAddress = datastore
					.run(Query.newEntityQueryBuilder().setKind(ORDER_DELIVERY_ADDRESS_KIND)
							.setFilter(PropertyFilter.eq(ORDER_DELIVERY_ID, deliveryEntity.getKey().getId())).build())
					.next();
		}

		orderBuilder.delivery(hidrateDeliveryFromEntity(deliveryEntity, deliveryAddress));

		OrderPayment payment = orderPaymentSaved;

		if (orderPaymentSaved == null) {
			payment = getPaymentForOrder(entity.getKey().getId());
		}

		orderBuilder.payment(payment);

		Iterator<Entity> savedDiscounts = discounts;
		if (savedDiscounts == null) {
			savedDiscounts = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_DISCOUNT_KIND)
					.setFilter(PropertyFilter.eq(ORDER_ID, entity.getKey().getId())).build());
		}

		while (savedDiscounts.hasNext()) {
			orderBuilder.discount(hidrateDiscountFromEntity(savedDiscounts.next()));
		}

		return orderBuilder.build();
	}

	private OrderPayment getPaymentForOrder(Long orderId) {
		QueryResults<Entity> paymentResults = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_PAYMENT_KIND)
				.setFilter(PropertyFilter.eq(ORDER_ID, orderId)).build());

		if (!paymentResults.hasNext()) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Order", "orderId = " + orderId));
		}

		Entity orderPaymentEntity = paymentResults.next();

		QueryResults<Entity> orderPaymentItemEntities = getOrderPaymentItemsEntities(
				orderPaymentEntity.getKey().getId());

		return hidrateOrderPaymentFromEntity(orderPaymentEntity, orderPaymentItemEntities);
	}

	private OrderDelivery hidrateDeliveryFromEntity(Entity deliveryEntity, Entity address) {
		OrderDeliveryBuilder deliveryBuilder = OrderDelivery.builder()
				.amount(deliveryEntity.getDouble(DaoConstants.AMOUNT)).orderDeliveryId(deliveryEntity.getKey().getId())
				.orderId(deliveryEntity.getLong(ORDER_ID))
				.createdByUserId(deliveryEntity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.createdOn(deliveryEntity.getTimestamp(DaoConstants.CREATED_ON))
				.dueDate(deliveryEntity.getTimestamp(DELIVERY_DUE_DATE))
				.status(OrderDeliveryStatus.valueOf(deliveryEntity.getString(DaoConstants.STATUS)));

		return deliveryBuilder.build();
	}

	private OrderDiscount hidrateDiscountFromEntity(Entity entity) {
		return OrderDiscount.builder().orderDiscountId(entity.getKey().getId()).orderId(entity.getLong(ORDER_ID))
				.amount(entity.getDouble(DaoConstants.AMOUNT)).createdOn(entity.getTimestamp(DaoConstants.CREATED_ON))
				.createdByUserId(entity.getLong(DaoConstants.CREATED_BY_USER_ID)).build();
	}

	private OrderItem hidrateItemFromEntity(Entity entity) {
		OrderItem item = OrderItem.builder().orderItemId(entity.getKey().getId()).orderId(entity.getLong(ORDER_ID))
				.quantity(entity.getLong(DaoConstants.QUANTITY)).skuId(entity.getLong(ITEM_SKU_ID))
				.price(entity.getDouble(DaoConstants.PRICE)).createdOn(entity.getTimestamp(DaoConstants.CREATED_ON))
				.createdByUserId(entity.getLong(DaoConstants.CREATED_BY_USER_ID)).build();
		return item;
	}

	@Override
	public Order getById(@NonNull Long orderId) {

		Key key = datastore.newKeyFactory().setKind(ORDER_KIND).newKey(orderId);

		QueryResults<Entity> entityResults = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_KIND)
				.setFilter(PropertyFilter.eq(DaoConstants.KEY_FIELD, key)).build());

		if (!entityResults.hasNext()) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Order", "orderId, businessId"));
		}

		return hidrateOrderFromEntity(entityResults.next());
	}

	@Override
	public List<Order> getOrders() {

		QueryResults<Entity> queryResults = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_KIND).build());
		List<Order> results = new ArrayList<Order>();

		while (queryResults.hasNext()) {
			results.add(hidrateOrderFromEntity(queryResults.next()));
		}

		return results;
	}

	protected Order getByIdOnly(@NonNull Long orderId) {

		Key key = datastore.newKeyFactory().setKind(ORDER_KIND).newKey(orderId);

		QueryResults<Entity> entityResults = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_KIND)
				.setFilter(PropertyFilter.eq(DaoConstants.KEY_FIELD, key)).build());

		if (!entityResults.hasNext()) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Order", "orderId, businessId"));
		}

		return hidrateOrderFromEntity(entityResults.next());
	}

	private List<OrderItem> hidrateItemList(QueryResults<Entity> entities) {
		if (entities == null) {
			return Collections.emptyList();
		}
		List<OrderItem> result = new ArrayList<OrderItem>();
		
		while (entities.hasNext()) {
			result.add(hidrateItemFromEntity(entities.next()));
		}
		
		return result;
	}

	public Order hidrateOrderFromEntity(Entity entity) {
		return hidrateOrderFromEntity(entity, null, null, null, null, null, null);
	}

	@Override
	public Order updateOrderDeliveryStatus(Long userId, Long orderId,
			OrderDeliveryStatusModificationData deliveryStatusData) {
		Transaction txn = datastore.newTransaction();

		try {

			QueryResults<Entity> entityResults = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_KIND)
					.setFilter(PropertyFilter.eq(DaoConstants.KEY_FIELD,
							datastore.newKeyFactory().setKind(ORDER_KIND).newKey(orderId)))
					.build());

			if (!entityResults.hasNext()) {
				throw new NotFoundDaoException(ErrorConstants
						.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, "Order", "orderId, businessId"));
			}

			Entity orderEntity = entityResults.next();

			Order order = hidrateHeaderOnlyOrderFromEntity(orderEntity);

			switch (order.getStatus()) {
			case CANCELLED:
				throw new InvalidArgumentsDaoException(ErrorConstants.formatError(ErrorConstants.CANT_SET_STATUS,
						"Order", "delivery de una Order cancelada"));
			case COMPLETE:
				throw new InvalidArgumentsDaoException(ErrorConstants.formatError(ErrorConstants.CANT_SET_STATUS,
						"Order", "delivery de una Order completada"));
			default:
			}

			Entity deliveryEntity = datastore.run(Query.newEntityQueryBuilder().setKind(DELIVERY_KIND)
					.setFilter(PropertyFilter.eq(ORDER_ID, orderEntity.getKey().getId())).build()).next();

			Entity updatedDeliveryEntity = Entity.newBuilder(deliveryEntity)
					.set(DaoConstants.STATUS, deliveryStatusData.getStatus().toString()).build();

			if (OrderDeliveryStatus.isFinalStatus(deliveryStatusData.getStatus())) {
				if (order.getStatus().equals(OrderStatus.NEW)) {
					orderEntity = Entity.newBuilder(orderEntity)
							.set(DaoConstants.STATUS, OrderStatus.IN_PROGRESS.toString()).build();
				} else if (OrderPaymentStatus.COMPLETE.equals(order.getPayment().getStatus())) {
					orderEntity = Entity.newBuilder(orderEntity)
							.set(DaoConstants.STATUS, OrderStatus.COMPLETE.toString()).build();
				}

				datastore.update(orderEntity);
			} else if (OrderPaymentStatus.NEW.equals(order.getPayment().getStatus())) {
				orderEntity = Entity.newBuilder(orderEntity).set(DaoConstants.STATUS, OrderStatus.NEW.toString())
						.build();

				datastore.update(orderEntity);
			}

			datastore.update(updatedDeliveryEntity);

			txn.commit();

			return hidrateOrderFromEntity(orderEntity);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

	@Override
	public Order createPaymentItem(@NonNull List<OrderPaymentItemCreationData> orderPaymentData) {

		Long orderId = orderPaymentData.stream().findFirst()
				.orElseThrow(() -> new InvalidArgumentsDaoException("the order payment data shouldn't be empty"))
				.getOrderId();
		QueryResults<Entity> entityResults = datastore.run(
				Query.newEntityQueryBuilder().setKind(ORDER_KIND).setFilter(PropertyFilter.eq(DaoConstants.KEY_FIELD,
						datastore.newKeyFactory().setKind(ORDER_KIND).newKey(orderId))).build());

		if (!entityResults.hasNext()) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Order", "orderId = " + orderId));
		}

		Entity orderEntity = entityResults.next();

		Order order = hidrateHeaderOnlyOrderFromEntity(orderEntity);

		OrderStatus originalOrderStatus = order.getStatus();

		if (OrderStatus.CANCELLED.equals(originalOrderStatus)) {
			throw new InvalidArgumentsDaoException(
					ErrorConstants.formatError(ErrorConstants.ELEMENT_ALREADY_CANCELED, "Order"));
		}

		Entity defaultOrderPaymentEntity = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_PAYMENT_KIND)
				.setFilter(PropertyFilter.eq(ORDER_ID, order.getOrderId())).build()).next();

		Iterator<Entity> orderPaymentItemEntities = getOrderPaymentItemsEntities(
				defaultOrderPaymentEntity.getKey().getId());

		OrderPayment defaultOrderPayment = hidrateHeaderOnlyOrderPaymentFromEntity(defaultOrderPaymentEntity);

		List<Entity> allItems = new ArrayList<Entity>();

		while (orderPaymentItemEntities.hasNext()) {
			allItems.add(orderPaymentItemEntities.next());
		}

		Transaction txn = datastore.newTransaction();

		try {

			Double subtotal = 0d;

			List<FullEntity<IncompleteKey>> itemsToSave = new ArrayList<FullEntity<IncompleteKey>>();

			for (OrderPaymentItemCreationData orderPaymentItemRegistrationData : orderPaymentData) {
				// Save items
				subtotal += orderPaymentItemRegistrationData.getAmount();

				FullEntity<IncompleteKey> entity = Entity.newBuilder()
						.setKey(datastore
								.allocateId(datastore.newKeyFactory().setKind(ORDER_PAYMENT_ITEM_KIND).newKey()))
						.set(ORDER_PAYMENT_ID, defaultOrderPayment.getOrderPaymentId())
						.set(DaoConstants.AMOUNT, orderPaymentItemRegistrationData.getAmount())
						.set(DaoConstants.DATE, DateUtils.timestampFrom(orderPaymentItemRegistrationData.getDate()))
						.setNull(DaoConstants.INVALID_FROM)
						.set(DaoConstants.CREATED_BY_USER_ID, orderPaymentItemRegistrationData.getUserId())
						.set(DaoConstants.CREATED_ON, Timestamp.now()).build();
				itemsToSave.add(entity);
			}

			defaultOrderPayment.addAmount(subtotal);

			if (order.getTotalAmount() > defaultOrderPayment.getTotalAmount()) {
				if (OrderStatus.NEW.equals(originalOrderStatus)) {
					order.setStatus(OrderStatus.IN_PROGRESS);
					defaultOrderPayment.setStatus(OrderPaymentStatus.PARTIAL);
				}
			} else {
				if (order.getTotalAmount().longValue() == defaultOrderPayment.getTotalAmount().longValue()) {
					if (OrderDeliveryStatus.isFinalStatus(order.getDelivery().getStatus())) {
						order.setStatus(OrderStatus.COMPLETE);
					}
					defaultOrderPayment.setStatus(OrderPaymentStatus.COMPLETE);

					if (OrderStatus.NEW.equals(originalOrderStatus)) {
						order.setStatus(OrderStatus.IN_PROGRESS);
					}
				} else {
					throw new InvalidArgumentsDaoException(
							ErrorConstants.formatError(ErrorConstants.SHOULD_BE_LESSER_EQUAL_THAN, "El total de pagos",
									"el coste total de la orden"));
				}
			}

			if (!order.getStatus().equals(originalOrderStatus)) {
				orderEntity = Entity.newBuilder(orderEntity).set(DaoConstants.STATUS, order.getStatus().toString())
						.build();
				datastore.update(orderEntity);
			}

			defaultOrderPaymentEntity = Entity.newBuilder(defaultOrderPaymentEntity)
					.set(DaoConstants.STATUS, defaultOrderPayment.getStatus().toString())
					.set(ORDER_PAYMENT_TOTAL_AMOUNT, defaultOrderPayment.getTotalAmount()).build();

			datastore.update(defaultOrderPaymentEntity);

			List<Entity> savedPaymentItems = datastore.put(itemsToSave.toArray(new FullEntity[itemsToSave.size()]));

			txn.commit();

			for (Entity entity : savedPaymentItems) {
				allItems.add(entity);
			}

			return hidrateOrderFromEntity(orderEntity,
					hidrateOrderPaymentFromEntity(defaultOrderPaymentEntity, allItems.iterator()), null, null, null,
					null, null);

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

	@Override
	public Order invalidatePaymentItem(@NonNull Long orderId, @NonNull List<Long> orderPaymentIdsToInvalidate) {

		Entity orderEntity = datastore.get(datastore.newKeyFactory().setKind(ORDER_KIND).newKey(orderId));

		QueryResults<Entity> paymentResults = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_PAYMENT_KIND)
				.setFilter(PropertyFilter.eq(ORDER_ID, orderId)).build());

		if (!paymentResults.hasNext()) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Order", "orderId = " + orderId));
		}

		Order order = hidrateOrderFromEntity(orderEntity);

		Entity orderPaymentEntity = paymentResults.next();
		OrderPayment defaultOrderPayment = hidrateHeaderOnlyOrderPaymentFromEntity(orderPaymentEntity);

		OrderStatus originalOrderStatus = order.getStatus();

		switch (order.getStatus()) {
		case COMPLETE:
			throw new InvalidArgumentsDaoException(ErrorConstants.PAYMENT_CANT_BE_DELETED_ORDER_COMPLETED);
		case CANCELLED:
			throw new InvalidArgumentsDaoException(ErrorConstants.PAYMENT_CANT_BE_DELETED_ORDER_CANCELLED);
		case NEW:
			throw new InvalidArgumentsDaoException(ErrorConstants.THERE_ARE_NO_PAYMENTS_TO_DELETE);
		default:
		}

		QueryResults<Entity> orderPaymentItemEntitiesIterator = getOrderPaymentItemsEntities(
				orderPaymentEntity.getKey().getId());
		LinkedHashMap<Long, Entity> finalOrderPaymentItemEntities = new LinkedHashMap<Long, Entity>();

		while (orderPaymentItemEntitiesIterator.hasNext()) {
			Entity entity = orderPaymentItemEntitiesIterator.next();

			finalOrderPaymentItemEntities.put(entity.getKey().getId(), entity);
		}

		List<Entity> orderPaymentItemEntitiesToInvalidate = new ArrayList<Entity>(orderPaymentIdsToInvalidate.size());

		for (int i = 0; i < orderPaymentIdsToInvalidate.size(); i++) {
			Entity paymentItem = finalOrderPaymentItemEntities.get(orderPaymentIdsToInvalidate.get(i));

			if (paymentItem == null) {
				throw new InvalidArgumentsDaoException(
						ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, "PaymentItem",
								"orderPaymentId = " + orderPaymentEntity.getKey().getId() + " orderPaymentItemId = "
										+ orderPaymentIdsToInvalidate.get(i)));
			}

			orderPaymentItemEntitiesToInvalidate.add(paymentItem);
			finalOrderPaymentItemEntities.remove(paymentItem.getKey().getId());
		}

		Transaction txn = datastore.newTransaction();

		try {

			Double totalAmount = 0d;

			Entity[] itemsToSave = new Entity[orderPaymentIdsToInvalidate.size()];
			int index = 0;

			for (Entity orderPaymentItemToSave : orderPaymentItemEntitiesToInvalidate) {
				totalAmount += orderPaymentItemToSave.getDouble(DaoConstants.AMOUNT);

				itemsToSave[index] = Entity.newBuilder(orderPaymentItemToSave)
						.set(DaoConstants.INVALID_FROM, Timestamp.now()).build();

				finalOrderPaymentItemEntities.remove(orderPaymentItemToSave.getKey().getId());

				index++;
			}

			defaultOrderPayment.addAmount(totalAmount * -1);

			if (defaultOrderPayment.getTotalAmount() == 0l) {
				if (OrderDeliveryStatus.isFinalStatus(order.getDelivery().getStatus())) {
					order.setStatus(OrderStatus.IN_PROGRESS);
				} else {
					order.setStatus(OrderStatus.NEW);
				}

				defaultOrderPayment.setStatus(OrderPaymentStatus.NEW);
			} else {
				if (order.getTotalAmount() < defaultOrderPayment.getTotalAmount()) {
					throw new InvalidArgumentsDaoException(ErrorConstants.SUM_OF_PAYMENTS_EXCEEDS_TOTAL_AMOUNT);
				} else {
					order.setStatus(OrderStatus.IN_PROGRESS);
					defaultOrderPayment.setStatus(OrderPaymentStatus.PARTIAL);
				}
			}

			if (!order.getStatus().equals(originalOrderStatus)) {
				orderEntity = Entity.newBuilder(orderEntity).set(DaoConstants.STATUS, order.getStatus().toString())
						.build();
				datastore.update(orderEntity);
			}

			datastore.update(itemsToSave);
			orderPaymentEntity = Entity.newBuilder(orderPaymentEntity)
					.set(DaoConstants.STATUS, defaultOrderPayment.getStatus().toString())
					.set(ORDER_PAYMENT_TOTAL_AMOUNT, defaultOrderPayment.getTotalAmount()).build();
			datastore.update(orderPaymentEntity);

			txn.commit();

			return hidrateOrderFromEntity(orderEntity, hidrateOrderPaymentFromEntity(orderPaymentEntity,
					finalOrderPaymentItemEntities.values().iterator()), null, null, null, null, null);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

	@Override
	public Order updateOrderStatus(Long userId, Long orderId, OrderStatusModificationData newOrderStatusData) {
		Transaction txn = datastore.newTransaction();

		try {

			QueryResults<Entity> entityResults = datastore.run(Query.newEntityQueryBuilder().setKind(ORDER_KIND)
					.setFilter(PropertyFilter.eq(DaoConstants.KEY_FIELD,
							datastore.newKeyFactory().setKind(ORDER_KIND).newKey(orderId)))
					.build());

			if (!entityResults.hasNext()) {
				throw new NotFoundDaoException(ErrorConstants
						.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, "Order", "orderId = " + orderId));
			}

			Entity orderEntity = entityResults.next();

			Order order = hidrateOrderFromEntity(orderEntity);

			if (OrderStatus.CANCELLED.equals(newOrderStatusData.getStatus())) {
				String errorString = null;

				switch (order.getStatus()) {
				case IN_PROGRESS:
					errorString = ErrorConstants.formatError(ErrorConstants.CANT_SET_STATUS_ORDER_SINCE,
							"que ya hay pagos realizados");
					break;
				case COMPLETE:
					errorString = ErrorConstants.formatError(ErrorConstants.CANT_SET_STATUS_ORDER_SINCE,
							"que la orden ya fue pagada y entregada");
					break;
				case CANCELLED:
					errorString = ErrorConstants.formatError(ErrorConstants.CANT_SET_STATUS_ORDER_SINCE,
							"que la orden ya fue cancelada");
					break;
				default:
					if (OrderDeliveryStatus.isFinalStatus(order.getDelivery().getStatus())) {
						errorString = ErrorConstants.formatError(ErrorConstants.CANT_SET_STATUS_ORDER_SINCE,
								"que la orden ya fue entregada");
					}
				}

				if (errorString != null) {
					throw new InvalidArgumentsDaoException(errorString);
				}

			} else {
				throw new InvalidArgumentsDaoException(ErrorConstants
						.formatError(ErrorConstants.THE_ONLY_VALID_VALUE_IS, OrderStatus.CANCELLED.toString()));
			}

			Entity updatedOrderEntity = Entity.newBuilder(orderEntity)
					.set(DaoConstants.STATUS, newOrderStatusData.getStatus().toString())
					.set(DaoConstants.CANCELLED_ON, Timestamp.now()).build();

			datastore.update(updatedOrderEntity);

			txn.commit();

			return hidrateOrderFromEntity(updatedOrderEntity);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

}
