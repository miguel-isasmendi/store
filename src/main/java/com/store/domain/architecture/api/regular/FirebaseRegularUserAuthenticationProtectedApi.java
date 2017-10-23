package com.store.domain.architecture.api.regular;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiIssuerAudience;
import com.google.api.server.spi.config.ApiNamespace;
import com.store.architecture.firebase.authenticator.FirebaseStoreAuthenticator;

@Api(name = "regular", version = "v1", namespace = @ApiNamespace(ownerDomain = "negrolas.com", ownerName = "negrolas.com", packagePath = ""),
		// [START_EXCLUDE]
		apiKeyRequired = AnnotationBoolean.TRUE, issuers = {
				@ApiIssuer(name = "firebase-reg-user", jwksUri = "https://www.googleapis.com/service_accounts/v1/metadata/x509/securetoken@system.gserviceaccount.com", issuer = "https://securetoken.google.com/negrolas-store") }, issuerAudiences = {
						@ApiIssuerAudience(name = "firebase-reg-user", audiences = {
								"negrolas-store" }) }, authenticators = { FirebaseStoreAuthenticator.class }
// [END_EXCLUDE]
)
/**
 * This is a class that holds the implementation common for all the APIs
 * implementing classes that have to validate their endpoints againts the
 * Firebase authentication service we have.
 */
public abstract class FirebaseRegularUserAuthenticationProtectedApi {

}
