package com.fdi.aplicacionWeb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/avisos/editar")
public class EdicionNoticiasController {
	
	@RequestMapping
	public String welcome(Model model) {
		model.addAttribute("greeting", "Bienvenido a la aplicaci�n de noticias de Fdi-UCM");
		model.addAttribute("tagline", "Seleccione la opci�n deseada");

		return "welcome";
	}
}
