package softnecessary.furgonok.utils;


import androidx.annotation.NonNull;
import softnecessary.furgonok.pojo.serialized.Apoderado;
import softnecessary.furgonok.pojo.serialized.Conductor;
import softnecessary.furgonok.pojo.serialized.FieldUpdatesApoderado;
import softnecessary.furgonok.pojo.serialized.FieldUpdatesConductor;
import softnecessary.furgonok.pojo.serialized.MiApodo;
import softnecessary.furgonok.pojo.serialized.MiPartidaApoderado;
import softnecessary.furgonok.pojo.serialized.MyLocation;

public final class Encryption2 {

  public Encryption2() {

  }

  public final MiPartidaApoderado decriptarMiPuntoPartidaApoderado(
      MiPartidaApoderado miPuntoPartida) {
    String latitudInicial = decriptarDato(miPuntoPartida.getLatitudInicial());
    String longitudInicial = decriptarDato(miPuntoPartida.getLongitudInicial());
    String latitudFinal = decriptarDato(miPuntoPartida.getLatitudFinal());
    String longitudFinal = decriptarDato(miPuntoPartida.getLongitudFinal());
    String nombreUsuario = decriptarDato(miPuntoPartida.getNombreUsuario());
    String apodo = decriptarDato(miPuntoPartida.getApodo());
    String lugar = decriptarDato(miPuntoPartida.getLugar());
    MiPartidaApoderado newPuntoPartida = new MiPartidaApoderado();
    newPuntoPartida.setLatitudInicial(latitudInicial);
    newPuntoPartida.setLongitudInicial(longitudInicial);
    newPuntoPartida.setLatitudFinal(latitudFinal);
    newPuntoPartida.setLongitudFinal(longitudFinal);
    newPuntoPartida.setNombreUsuario(nombreUsuario);
    newPuntoPartida.setApodo(apodo);
    newPuntoPartida.setLugar(lugar);
    return newPuntoPartida;
  }

  /*public MiPartidaConductor decriptarMiPuntoPartidaConductor(MiPartidaConductor miPuntoPartida) {
    String latitud = decriptarDato(miPuntoPartida.getLatitud());
    String longitud = decriptarDato(miPuntoPartida.getLongitud());
    String nombreUsuario = decriptarDato(miPuntoPartida.getNombreUsuario());
    String apodo = decriptarDato(miPuntoPartida.getApodo());
    MiPartidaConductor newPuntoPartida = new MiPartidaConductor();
    newPuntoPartida.setLatitud(latitud);
    newPuntoPartida.setLongitud(longitud);
    newPuntoPartida.setNombreUsuario(nombreUsuario);
    newPuntoPartida.setApodo(apodo);
    return newPuntoPartida;
  }*/

  public final MyLocation decriptarLocation(MyLocation myLocation) {
    MyLocation location = new MyLocation();
    String latitude = decriptarDato(myLocation.getLatitud());
    String longitude = decriptarDato(myLocation.getLongitud());
    String nombreUsuario = decriptarDato(myLocation.getNombreUsuario());
    location.setLatitud(latitude);
    location.setLongitud(longitude);
    location.setNombreUsuario(nombreUsuario);
    return location;
  }


  @NonNull
  @Override
  public String toString() {
    return "Encryption2{}";
  }

  public final String encriptarDato(String string) {
    if (string == null) {
      return "";
    }
    if (string.equals("")) {
      return "";
    } else {

      String resultado;
      try {
        resultado = AESUtils.encrypt(string);
      } catch (Exception e) {

        resultado = "";
      }
      return resultado;
    }

  }

  private String decriptarDato(String string) {
    if (string == null) {
      return "";
    }
    if (string.equals("")) {
      return "";
    } else {
      String resultado;
      try {
        resultado = AESUtils.decrypt(string);
      } catch (Exception e) {

        resultado = "";
      }
      return resultado;
    }


  }

  public final MiApodo decriptarMiApodo(MiApodo miApodo) {
    String nombreUsuario = decriptarDato(miApodo.getNombreUsuario());
    String apodo = decriptarDato(miApodo.getApodo());
    MiApodo newMiApodo = new MiApodo();
    newMiApodo.setNombreUsuario(nombreUsuario);
    newMiApodo.setApodo(apodo);
    return newMiApodo;
  }

  public final Apoderado decriptarDataApoderado(Apoderado objApoderado) {
    String apodo = decriptarDato(objApoderado.getApodo());
    String correo = decriptarDato(objApoderado.getCorreo());
    String password = decriptarDato(objApoderado.getPassword());
    String nombreUsuario = decriptarDato(objApoderado.getNombreUsuario());
    String fechaNac = decriptarDato(objApoderado.getFechaNac());
    //Conductor conductor = objApoderado.getConductor();
    String passwordChat = decriptarDato(objApoderado.getPasswordChat());
    String latitudInicial = decriptarDato(objApoderado.getLatitudInicial());
    String longitudInicial = decriptarDato(objApoderado.getLongitudInicial());
    String latitudFinal = decriptarDato(objApoderado.getLatitudFinal());
    String longitudFinal = decriptarDato(objApoderado.getLongitudFinal());
    String lugar = decriptarDato(objApoderado.getLugar());
    //String authToken = decriptarDato(objApoderado.getAuthToken());
    Apoderado objApod1 = new Apoderado();
    Integer miId = objApoderado.getId();
    String authToken = objApoderado.getAuthToken();
    objApod1.setPasswordChat(passwordChat);
    objApod1.setAuthToken(authToken);
    objApod1.setId(miId);
    objApod1.setApodo(apodo);
    objApod1.setCorreo(correo);
    objApod1.setPassword(password);
    objApod1.setNombreUsuario(nombreUsuario);
    objApod1.setFechaNac(fechaNac);
    objApod1.setLugar(lugar);
    objApod1.setLatitudInicial(latitudInicial);
    objApod1.setLongitudInicial(longitudInicial);
    objApod1.setLatitudFinal(latitudFinal);
    objApod1.setLongitudFinal(longitudFinal);
    //objApoderado.setAuthToken(authToken);

    return objApod1;

  }

  public final FieldUpdatesApoderado encriptarFieldUpdateApoderado(
      FieldUpdatesApoderado fieldUpdate) {
    FieldUpdatesApoderado newField = new FieldUpdatesApoderado();
    String password = encriptarDato(fieldUpdate.getPassword());
    String latitudInicial = encriptarDato(fieldUpdate.getLatitudInicial());
    String longitudInicial = encriptarDato(fieldUpdate.getLongitudInicial());
    String latitudFinal = encriptarDato(fieldUpdate.getLatitudFinal());
    String longitudFinal = encriptarDato(fieldUpdate.getLongitudFinal());
    String lugar = encriptarDato(fieldUpdate.getLugar());
    newField.setLatitudInicial(latitudInicial);
    newField.setLongitudInicial(longitudInicial);
    newField.setLatitudFinal(latitudFinal);
    newField.setLongitudFinal(longitudFinal);
    newField.setPassword(password);
    newField.setLugar(lugar);
    return newField;
  }

  public final FieldUpdatesConductor encriptarFieldUpdateConductor(
      FieldUpdatesConductor fieldUpdate) {
    FieldUpdatesConductor newField = new FieldUpdatesConductor();
    String password = encriptarDato(fieldUpdate.getPassword());
    String latitud = encriptarDato(fieldUpdate.getLatitud());
    String longitud = encriptarDato(fieldUpdate.getLongitud());
    newField.setPassword(password);
    newField.setLatitud(latitud);
    newField.setLongitud(longitud);

    return newField;
  }

  public final Apoderado encriptarDataApoderado(Apoderado objApoderado) {

    String apodo = encriptarDato(objApoderado.getApodo());
    String correo = encriptarDato(objApoderado.getCorreo());
    String password = encriptarDato(objApoderado.getPassword());
    String nombreUsuario = encriptarDato(objApoderado.getNombreUsuario());
    String fechaNac = encriptarDato(objApoderado.getFechaNac());
    //Conductor conductor = objApoderado.getConductor();
    String passwordChat = encriptarDato(objApoderado.getPasswordChat());
    String latitudInicial = encriptarDato(objApoderado.getLatitudInicial());
    String longitudInicial = encriptarDato(objApoderado.getLongitudInicial());
    String latitudFinal = encriptarDato(objApoderado.getLatitudFinal());
    String longitudFinal = encriptarDato(objApoderado.getLongitudFinal());
    String lugar = encriptarDato(objApoderado.getLugar());
    //String authToken = encriptarDato(objApoderado.getAuthToken());
    Apoderado apoderado = new Apoderado();
    Integer miId = objApoderado.getId();
    String authToken = objApoderado.getAuthToken();
    apoderado.setPasswordChat(passwordChat);
    apoderado.setAuthToken(authToken);
    apoderado.setId(miId);
    apoderado.setApodo(apodo);
    apoderado.setCorreo(correo);
    apoderado.setPassword(password);
    apoderado.setNombreUsuario(nombreUsuario);
    apoderado.setFechaNac(fechaNac);
    apoderado.setLugar(lugar);
    apoderado.setLatitudInicial(latitudInicial);
    apoderado.setLongitudInicial(longitudInicial);
    apoderado.setLatitudFinal(latitudFinal);
    apoderado.setLongitudFinal(longitudFinal);

    //objApoderado.setAuthToken(authToken);

    return apoderado;

  }

  public final Conductor decriptarDataConductor(Conductor objConductor) {
    String apodo = decriptarDato(objConductor.getApodo());
    String correo = decriptarDato(objConductor.getCorreo());
    String password = decriptarDato(objConductor.getPassword());
    String nombreUsuario = decriptarDato(objConductor.getNombreUsuario());
    String fechaNac = decriptarDato(objConductor.getFechaNac());
    String passwordChat = decriptarDato(objConductor.getPasswordChat());
    String latitud = decriptarDato(objConductor.getLatitud());
    String longitud = decriptarDato(objConductor.getLongitud());
    //String authToken = decriptarDato(objConductor.getAuthToken());
    String authToken = objConductor.getAuthToken();
    Integer miId = objConductor.getId();
    Conductor conductor = new Conductor();
    conductor.setAuthToken(authToken);
    conductor.setId(miId);
    conductor.setApodo(apodo);
    conductor.setCorreo(correo);
    conductor.setPassword(password);
    conductor.setNombreUsuario(nombreUsuario);
    conductor.setFechaNac(fechaNac);
    conductor.setPasswordChat(passwordChat);
    conductor.setLatitud(latitud);
    conductor.setLongitud(longitud);
    return conductor;
  }

  public final Conductor encriptarDataConductor(Conductor objConductor) {

    String apodo = encriptarDato(objConductor.getApodo());
    String correo = encriptarDato(objConductor.getCorreo());
    String password = encriptarDato(objConductor.getPassword());
    String nombreUsuario = encriptarDato(objConductor.getNombreUsuario());
    String fechaNac = encriptarDato(objConductor.getFechaNac());
    String passwordChat = encriptarDato(objConductor.getPasswordChat());
    String latitud = encriptarDato(objConductor.getLatitud());
    String longitud = encriptarDato(objConductor.getLongitud());
    String authToken = objConductor.getAuthToken();
    Integer miId = objConductor.getId();
    //String authToken = encriptarDato(objConductor.getAuthToken());
    Conductor cond = new Conductor();
    cond.setApodo(apodo);
    cond.setCorreo(correo);
    cond.setPassword(password);
    cond.setNombreUsuario(nombreUsuario);
    cond.setFechaNac(fechaNac);
    cond.setPasswordChat(passwordChat);
    cond.setLatitud(latitud);
    cond.setLongitud(longitud);
    cond.setId(miId);
    cond.setAuthToken(authToken);
    return cond;

  }
}
