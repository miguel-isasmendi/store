package com.store.domain.api.regular;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.GET;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.POST;

import java.util.List;
import java.util.stream.Collectors;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.inject.Inject;
import com.store.architecture.exception.annotation.ExceptionMapping;
import com.store.architecture.exception.dao.ConflictDaoException;
import com.store.architecture.exception.dao.InvalidArgumentsDaoException;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.exception.validation.RequestBusinessValidationException;
import com.store.architecture.exception.validation.UnexpectedBuildException;
import com.store.architecture.exception.validation.UnexpectedValidationException;
import com.store.architecture.model.ResponseListContainer;
import com.store.architecture.model.ResponseListContainer.ResponseListContainerBuilder;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.architecture.api.regular.FirebaseRegularUserAuthenticationProtectedApi;
import com.store.domain.model.bundle.build.coordinator.BundleBuildCoordinator;
import com.store.domain.model.bundle.dto.BundleCreationDto;
import com.store.domain.model.bundle.dto.BundleDto;
import com.store.domain.model.product.build.coordinator.ProductBuildCoordinatorProvider;
import com.store.domain.model.product.build.validator.ProductCreationValidatorProvider;
import com.store.domain.model.product.data.FullProductData;
import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.dto.FullProductDto;
import com.store.domain.model.product.dto.ProductCreationDto;
import com.store.domain.model.product.dto.ProductCreationListDto;
import com.store.domain.model.sku.build.coordinator.SkuBuildCoordinatorProvider;
import com.store.domain.model.sku.build.validator.SkuCreationValidatorProvider;
import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.dto.SkuCreationDto;
import com.store.domain.model.sku.dto.SkuDto;
import com.store.domain.model.user.data.UserData;
import com.store.domain.service.catalog.CatalogCoordinatorService;
import com.store.domain.service.user.UserService;

import lombok.NonNull;

@ExceptionMapping(from = RequestBusinessValidationException.class, to = BadRequestException.class)
@ExceptionMapping(from = UnexpectedValidationException.class, to = BadRequestException.class)
@ExceptionMapping(from = UnexpectedBuildException.class, to = BadRequestException.class)
@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
@ExceptionMapping(from = ConflictDaoException.class, to = ConflictException.class)
public class CatalogApi extends FirebaseRegularUserAuthenticationProtectedApi {
	private CatalogCoordinatorService catalogCoordinatorService;
	private UserService userService;

	@Inject
	public CatalogApi(@NonNull CatalogCoordinatorService catalogCoordinatorService, @NonNull UserService userService) {
		this.catalogCoordinatorService = catalogCoordinatorService;
		this.userService = userService;
	}

	@ApiMethod(httpMethod = POST, path = "/store/products")
	public ResponseListContainer<FullProductDto> createProduct(@NonNull User user,
			@NonNull ProductCreationListDto productRegistrationDtoList) throws ServiceException {

		List<ProductCreationData> productsRegistrationDatas = null;
		UserData storeUser = this.userService.getByFirebaseId(user.getId());

		ObjectBuildConversionOverseer<ProductCreationDto, ProductCreationData> translationOverseer = ObjectBuildConversionOverseer
				.<ProductCreationDto, ProductCreationData>builder()
				.preBuildValidator(ProductCreationValidatorProvider::validateDtoToDataTranslation)
				.buildClosure(ProductBuildCoordinatorProvider::buildToData).build();

		productsRegistrationDatas = productRegistrationDtoList.getItems().stream()
				.map(productCreationDto -> translationOverseer.setInputObject(productCreationDto).execute())
				.collect(Collectors.toList());

		List<FullProductData> createdProducts = catalogCoordinatorService.createProduct(storeUser.getUserId(),
				productsRegistrationDatas);

		ResponseListContainerBuilder<FullProductDto> responseContainerBuilder = ResponseListContainer
				.<FullProductDto>builder();

		responseContainerBuilder.items(
				createdProducts.stream().map(ProductBuildCoordinatorProvider::toFullDto).collect(Collectors.toList()));

		return responseContainerBuilder.build();
	}

	@ApiMethod(httpMethod = GET, path = "/store/products/{product_id}")
	public FullProductDto getProduct(User user, @Named("product_id") Long productId) throws ServiceException {

		return ProductBuildCoordinatorProvider.toFullDto(catalogCoordinatorService.getProductById(productId));
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.GET, path = "/store/products")
	public ResponseListContainer<Object> getProductList(@NonNull User user,
			@Nullable @Named("includeSkus") Boolean shouldIncludeSkus) throws ServiceException {

		Boolean haveToDeliverFatRepresentation = Boolean.TRUE.equals(shouldIncludeSkus);
		ResponseListContainerBuilder<Object> productsResponse = ResponseListContainer.<Object>builder();

		if (haveToDeliverFatRepresentation) {
			productsResponse.items(catalogCoordinatorService.getFullProducts().stream()
					.map(ProductBuildCoordinatorProvider::toFullDto).collect(Collectors.toList()));
		} else {
			productsResponse.items(catalogCoordinatorService.getProductService().getProducts());
		}

		return productsResponse.build();
	}

	@ApiMethod(httpMethod = POST, path = "/store/products/{product_id}/skus")
	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = NotFoundException.class)
	public SkuDto createSku(@NonNull User user, @NonNull @Named("product_id") Long productId,
			@NonNull SkuCreationDto skuCreationDto) throws ServiceException {
		UserData storeUser = this.userService.getByFirebaseId(user.getId());

		skuCreationDto.setProductId(productId);

		SkuCreationData skuCreationData = ObjectBuildConversionOverseer.<SkuCreationDto, SkuCreationData>builder()
				.inputObject(skuCreationDto)
				.preBuildValidator(SkuCreationValidatorProvider::validateDtoToDataTranslation)
				.buildClosure(overseer -> SkuBuildCoordinatorProvider.toData(overseer.getInputObject())).build()
				.execute();

		return SkuBuildCoordinatorProvider
				.toDto(catalogCoordinatorService.getSkuService().create(storeUser.getUserId(), skuCreationData));
	}

	@ApiMethod(httpMethod = POST, path = "/store/bundles")
	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = NotFoundException.class)
	public BundleDto createBundle(@NonNull User user, @NonNull BundleCreationDto bundleCreationDto)
			throws ServiceException {
		UserData storeUser = this.userService.getByFirebaseId(user.getId());

		return BundleBuildCoordinator.toDto(catalogCoordinatorService.createBundle(storeUser.getUserId(),
				BundleBuildCoordinator.toData(bundleCreationDto)));
	}

	@ApiMethod(httpMethod = GET, path = "/store/bundles/{bundle_id}")
	public BundleDto getBundle(@NonNull User user, @NonNull @Named("bundle_id") Long bundleId) throws ServiceException {
		UserData storeUser = this.userService.getByFirebaseId(user.getId());

		return BundleBuildCoordinator.toDto(catalogCoordinatorService.getBundleById(storeUser.getUserId(), bundleId));
	}
}
