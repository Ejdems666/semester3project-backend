package org.cba.rest.resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.cba.config.Config;
import org.cba.model.entities.Role;
import org.cba.model.entities.User;
import org.cba.model.exceptions.ResourceNotFoundException;
import org.cba.model.facade.LoginFacade;
import org.cba.rest.error.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by adam on 11/15/2017.
 */
@Path("login")
public class Login {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws JOSEException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String username = json.get("username").getAsString();
        String password = json.get("password").getAsString();
        LoginFacade loginFacade = new LoginFacade();
        try {
            User user = loginFacade.authenticateUser(username,password);
            String token = createToken(user);
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("username", username);
            responseJson.addProperty("token", token);
            return Response.ok(new Gson().toJson(responseJson)).build();
        } catch (ResourceNotFoundException | LoginFacade.IncorrectPasswordException e) {
            return new ErrorResponse(401,"Username or password is incorrect!").build();
        }
    }

    private String createToken(User user) throws JOSEException {
        List<String> roles = getRolesAsStringList(user);
        String issuer = "semester3project-fbbc";

        JWSSigner signer = new MACSigner(Config.SECRET_SIGNATURE);
        Date now = new Date();
        Date after7Days = new Date(now.getTime() + 60 * 60 * 24 * 7);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .claim("username", user.getUsername())
                .claim("roles", roles)
                .claim("issuer", issuer)
                .issueTime(now)
                .expirationTime(after7Days)
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @NotNull
    private List<String> getRolesAsStringList(User user) {
        List<String> list = new ArrayList<>();
        for (Role role : user.getRoles()) {
            list.add(role.getName());
        }
        return list;
    }
}
