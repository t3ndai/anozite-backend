package com.anozite.endpoints

import java.security.Principal;
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.InternalServerErrorException
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.JsonWebToken
import com.anozite.utils.generateToken

@Path("/secured")
class token {

    @Inject
    lateinit  var jwt: JsonWebToken

    @GET()
    @Path("permit-all")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(@Context ctx: SecurityContext): String {
        return getResponseString(ctx)
    }

    private fun getResponseString(ctx: SecurityContext): String {
        var name = ""
        name = if (ctx.userPrincipal == null) {
            "anonymous"
        } else if (!ctx.userPrincipal.name.equals(jwt.name)) {
            throw InternalServerErrorException("Principal and JsonWebToken names do not match")
        } else {
            ctx.userPrincipal.name
        }

        return "Hello $name, isHTTPS: ${ctx.isSecure}, authScheme: ${ctx.authenticationScheme}, hasJWT: ${hasJWT()}"
    }


    @GET
    @Path("roles-allowed")
    @RolesAllowed("User", "Admin")
    fun helloRolesAllowed(@Context ctx: SecurityContext): String {
        return getResponseString(ctx) + "birthDate: ${jwt.getClaim<String>("birthate")}"
    }

    @GET
    @Path("sign-in")
    fun signIn(@Context ctx: SecurityContext): String {
        var userId = "239230"
        var jwtToken = generateToken(userId = userId, roles = arrayOf("User", "Admin"))
        return jwtToken
    }



    private fun hasJWT(): Boolean {
        return jwt.claimNames != null
    }
}