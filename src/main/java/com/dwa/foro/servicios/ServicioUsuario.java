package com.dwa.foro.servicios;
import java.security.MessageDigest;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dwa.foro.modelo.Usuario;

@Service
public class ServicioUsuario {
	@Autowired
	private RepoUsuario repoUsuario;
	private String Mensaje;
	
	public String getMensaje()
	{
		return Mensaje;
	}
	
	public void setMensaje(String mensaje) 
	{
		Mensaje= mensaje;
	}
	
	public boolean validar(String correoe, String clave, HttpSession sesion)
	{
		Usuario usr= repoUsuario.validar(correoe, encriptar(clave));
		if(usr !=null)
		{
			sesion.setAttribute("iduser", usr.getId());
			Mensaje = usr.getNombre()+ " - " + usr.getCorreoe();
			return true;
		}
		Mensaje= "Datos de acceso incorrecto";
		return false;
	}
	
	private byte[] encriptar(String clave)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update(clave.getBytes("utf8"));
			return digest.digest();
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public boolean agregar(Usuario u, String clave, HttpSession sesion)
	{
		try
		{
			sesion.setAttribute("iduser", u.getId());
			u.setClaveacceso(encriptar(clave));
			repoUsuario.save(u);
			this.Mensaje= "Datos del usuario almacenados con exito";
			return true;
		}
		catch(Exception e)
		{
			this.Mensaje= "Error al registrar al usuario \n" + e.getMessage() + " Error 2: " + e.getStackTrace();
			return false;
		}
	}
	
	public Usuario buscar(int id)
	{
		try
		{
			Usuario usuario= repoUsuario.findById(id).get();
			return usuario == null? new Usuario(): usuario;
		}
		catch(Exception e)
		{
			this.Mensaje= "No se pudo realizar la busqueda del usuario";
			return null;
		}
	}
}