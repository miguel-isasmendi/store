package com.store.domain.architecture.api.admin;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiIssuerAudience;
import com.google.api.server.spi.config.ApiNamespace;
import com.store.architecture.firebase.authenticator.FirebaseStoreAuthenticator;

@Api(name = "admin", version = "v1", namespace = @ApiNamespace(ownerDomain = "negrolas.com", ownerName = "negrolas.com", packagePath = ""),
		// [START_EXCLUDE]
		apiKeyRequired = AnnotationBoolean.TRUE, issuers = {
				@ApiIssuer(name = "firebase-store-admin", jwksUri = "https://www.googleapis.com/service_accounts/v1/metadata/x509/securetoken@system.gserviceaccount.com", issuer = "https://securetoken.google.com/store-admin") }, issuerAudiences = {
						@ApiIssuerAudience(name = "firebase-store-admin", audiences = {
								"store-admin" }) }, authenticators = { FirebaseStoreAuthenticator.class }
// [END_EXCLUDE]
)
public abstract class FirebaseAdminAuthenticationProtectedApi {

}
