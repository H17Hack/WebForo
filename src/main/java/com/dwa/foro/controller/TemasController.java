package com.dwa.foro.controller;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dwa.foro.modelo.Replica;
import com.dwa.foro.modelo.Tema;
import com.dwa.foro.modelo.Usuario;
import com.dwa.foro.servicios.ServicioReplica;
import com.dwa.foro.servicios.ServicioTema;
import com.dwa.foro.servicios.ServicioUsuario;

@Controller
public class TemasController { 
	@Autowired
	ServicioUsuario servicioUsuario;
	@Autowired
	ServicioTema serviciotema;
	@Autowired
	ServicioReplica servicioreplica;
	
	@GetMapping("/temas/listar")
	public String listar(Model modelo)
	{
		Iterable<Tema> temas = serviciotema.verTodos();
		if(temas!=null && temas.iterator().hasNext())
		{
			modelo.addAttribute("temas", temas);
		}
		else
		{
			modelo.addAttribute("error","Aun no hay temas registrados....");
		}
		modelo.addAttribute("tema", new Tema());
		if(!serviciotema.getMensaje().equals(""))
		{
			modelo.addAttribute("error", serviciotema.getMensaje());
		}
		return "temas/listar";
	}
	
	@PostMapping("/temas/agregar")
	public String agregar(Model modelo, @ModelAttribute Tema tema, HttpSession sesion)
	{
		int iduser = Integer.parseInt(sesion.getAttribute("iduser").toString());
		Usuario autor= servicioUsuario.buscar(iduser);
		if(autor == null || autor.getId()== 0)
		{
			modelo.addAttribute("error", servicioUsuario.getMensaje());
		}
		else
		{
			tema.setUsuario(autor);
			if(!serviciotema.agregar(tema));
				modelo.addAttribute("error", serviciotema.getMensaje());
			
		}
		return "redirect:/temas/listar";
	}
	
	@GetMapping("/temas/detalles")
	public String detalles(Model modelo, HttpSession sesion)
	{
		Iterable<Tema> temas= serviciotema.verTodos();
		if(temas.iterator().hasNext())
		{
			modelo.addAttribute("temas", temas);
		}
		else
		{
			modelo.addAttribute("error", "Aun no hay temas registrados....");
		}
		modelo.addAttribute("tema", new Tema());
		if(!serviciotema.getMensaje().equals(""))
		{
			modelo.addAttribute("error", serviciotema.getMensaje());
		}
		return "temas/listar";
	}
	
	@GetMapping("/temas/detalles/{id}")
	public String detalles(Model modelo, @PathVariable int id)
	{
		Tema tema = serviciotema.buscar(id);
		modelo.addAttribute("tema", tema.getTitulo());
		Iterable<Replica> replicas = servicioreplica.filtrarPorTema(id);
		if(replicas!=null && replicas.iterator().hasNext())
		{
			modelo.addAttribute("replicas", replicas);
		}
		else
		{
			modelo.addAttribute("error", "Aun no hay replicas registradas...");
		}
		Replica nueva= new Replica();
		nueva.setTema(tema);
		modelo.addAttribute("replica", nueva);
		if(!servicioreplica.getMensaje().equals(""))
		{
			modelo.addAttribute("error", servicioreplica.getMensaje());
		}
		return "temas/detalles";
	}
	
	@PostMapping("/temas/agregarreplica")
	public String agregar(Model modelo, @ModelAttribute Replica replica, HttpSession sesion)
	{
		int iduser = Integer.parseInt(sesion.getAttribute("iduser").toString());
		Usuario autor = servicioUsuario.buscar(iduser);
		if(autor == null || autor.getId() == 0)
		{
			modelo.addAttribute("error", servicioUsuario.getMensaje());
		}
		else
		{
			replica.setUsuario(autor);
			if(!servicioreplica.agregar(replica));
			modelo.addAttribute("error", servicioreplica.getMensaje());
		}
		return "redirect:/temas/detalles/"+ replica.getTema().getId();
	}
}
