package com.wso2.ob.webapp.utility.services;

import com.wso2.ob.webapp.utility.utils.JWKSUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST API to manage user admin functions.
 *
 * @since 1.0.0
 */
@Path("/jwks")
@Produces(MediaType.APPLICATION_JSON)
public class JWKSService {

    private static Logger log = LoggerFactory.getLogger(JWKSService.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/heartbeat")
    public Response heartBeatCall() {
        if(log.isDebugEnabled()) {
            log.debug("JWKSService: heartbeat request");
        }
        return Response.status(Response.Status.OK)
                .entity("Bank's JWKS service up!")
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/endpoint")
    public Response getJwksEndpoint() {
        if(log.isDebugEnabled()) {
            log.debug("JWKSService: retrieving JWKS");
        }
        JSONObject jwks = new JSONObject();
        JSONArray allCerts;
        try {
            allCerts = JWKSUtil.getOBbieJwksEndpointDetails();
            for (int i = 0; i < JWKSUtil.getBankCerts().length(); i++) {
                allCerts.put(JWKSUtil.getBankCerts().get(i));
            }
        } catch (Exception e) {
            log.error("An error occurred trying to create JWKS output "+e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred trying to create JWKS output")
                    .build();
        }
        jwks.put("keys",allCerts);
        return Response.status(Response.Status.OK)
                .entity(jwks.toString())
                .build();
    }
}