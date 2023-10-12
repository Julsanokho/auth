package com.sntech.auth.controller

import com.sntech.auth.dtos.LoginDTO
import com.sntech.auth.dtos.Message
import com.sntech.auth.dtos.RegisterDTO
import com.sntech.auth.models.User
import com.sntech.auth.services.UserServices
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(("api/v1"))
class AuthController(private val userServices: UserServices) {
    @PostMapping("/register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<User>{
        val user = User()
        user.name = body.name
        user.email = body.email
        user.password = body.password
        return ResponseEntity.ok(this.userServices.save(user))
    }

    @PostMapping("/login")
    fun login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        // Fin user by email
        val user = this.userServices.finByEmail(body.email)
                // check if the username is not null
                ?:return ResponseEntity.badRequest().body(Message("User not found!"))
        // check if the password match
        if (!user.comparePassword(body.password)){
            return ResponseEntity.badRequest().body(Message("Invalid password!"))
        }

        val issuer = user.id.toString()

        // Create a secure random key or load it from a secure location
        val secretKey = "secret"

        val jwt = Jwts.builder()
                .setIssuer(issuer)
                .setExpiration(Date(System.currentTimeMillis() + 60 * 24  * 1000)) // 60 * 24 hours = 1 day
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact()

        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true

        response.addCookie(cookie)

        return ResponseEntity.ok(Message("Success"))
    }

    @GetMapping("/user")
    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any>{
          try {
              if (jwt == null){
                  return ResponseEntity.status(401).body(Message("Unauthenticated"))
              }

              val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
              return ResponseEntity.ok(this.userServices.getById(body.issuer.toInt()))

          } catch (e: Exception){
              return ResponseEntity.status(401).body(Message("Unauthenticated"))
          }
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any>{
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0

        response.addCookie(cookie)

        return ResponseEntity.ok(Message("Success"))
    }
}
