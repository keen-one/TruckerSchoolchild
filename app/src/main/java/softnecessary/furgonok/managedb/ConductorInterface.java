package softnecessary.furgonok.managedb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ConductorInterface {

  /*
  @GET("puddle/usaring/")
  Call<String> getApodoByUsername(@Query(value = "id", encoded = true) int id,
      @Query(value = "auth_token", encoded = true) String authToken,
      @Query(value = "username", encoded = true) String username);*/

  @GET("logotipo/")
  Call<String> logear(
      @Query(value = "email", encoded = true) String correo,
      @Query(value = "contrasena", encoded = true) String password);

  @GET("napolitana/vida/")
  Call<String> existeUsername(@Query(value = "username", encoded = true) String username);

  @GET("renovado/")
  Call<String> update(@Query(value = "id", encoded = true) int id,
      @Query(value = "auth_token", encoded = true) String authToken,
      @Query(value = "email", encoded = true) String email,
      @Query(value = "contrasena", encoded = true) String contrasena,
      @Query(value = "email1", encoded = true) String email1,
      @Query(value = "latitud", encoded = true) String latitud,
      @Query(value = "longitud", encoded = true) String longitud,
      @Query(value = "password", encoded = true) String password
  );

  @GET("correr/")
  Call<String> getByCorreo(@Query(value = "id", encoded = true) int id,
      @Query(value = "auth_token", encoded = true) String authToken,
      @Query(value = "email", encoded = true) String email,
      @Query(value = "contrasena", encoded = true) String contrasena,
      @Query(value = "correo", encoded = true) String correo);


  @GET("poso/")
  Call<String> store(@Query(value = "apodo", encoded = true) String apodo,
      @Query(value = "correo", encoded = true) String correo,
      @Query(value = "nombreUsuario", encoded = true) String nombreUsuario,
      @Query(value = "fechaNac", encoded = true) String fechaNac,
      @Query(value = "password", encoded = true) String password);


  @GET("correr/terminator/")
  Call<String> deleteByCorreo(@Query(value = "id", encoded = true) int id,
      @Query(value = "auth_token", encoded = true) String authToken,
      @Query(value = "email", encoded = true) String email,
      @Query(value = "contrasena", encoded = true) String contrasena,
      @Query(value = "correo", encoded = true) String correo);

  @GET("pua/")
  Call<String> getLocalizacion(@Query(value = "id", encoded = true) int id,
      @Query(value = "auth_token", encoded = true) String authToken,
      @Query(value = "username", encoded = true) String username);
}
