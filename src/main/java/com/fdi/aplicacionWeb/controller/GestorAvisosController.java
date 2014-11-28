package com.fdi.aplicacionWeb.controller;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fdi.aplicacionWeb.domain.Aviso;
import com.fdi.aplicacionWeb.service.AvisoService;

@Controller
@RequestMapping("/avisos/gestor")
public class GestorAvisosController {

	@Autowired
	AvisoService avisoService;

	@RequestMapping
	public String gestor(Model model) {
		model.addAttribute("avisos", avisoService.getAllAvisos());
		return "gestorAvisos";		
	}

	@RequestMapping("/editar")
	public String editarAvisos(Model model) {
		model.addAttribute("avisos", avisoService.getAllAvisos());
		return "editarAvisos";
	}

	@RequestMapping("/eliminar")
	public String eliminarAviso(@RequestParam("id") String avisoID, Model model) {
		avisoService.eliminarAviso(avisoID);
		return "redirect:/avisos/ver";	
	}


	@RequestMapping(value="/crear", method = RequestMethod.GET)	
	public String formularioCreadorAvisos(@ModelAttribute("aviso") Aviso aviso, Model model) {
		//return "creadorAvisos";
		Date date = new Date(System.currentTimeMillis());
		
		String []  meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
		ArrayList<Integer> dias = new ArrayList<Integer>();
		for (int i = 1; i <= 31; i++){
			dias.add(new Integer(i));
		}
		ArrayList<Integer> anyos = new ArrayList<Integer>();
		for (int i = 2014; i <= 2015; i++){
			anyos.add(new Integer(i));
		}
		ArrayList<Integer> horas = new ArrayList<Integer>();
		for (int i = 0; i <= 23; i++){
			horas.add(new Integer(i));
		}
		ArrayList<Integer> minutos = new ArrayList<Integer>();
		for (int i = 0; i <= 59; i++){
			minutos.add(new Integer(i));
		}
		ArrayList<Integer> segundos = new ArrayList<Integer>();
		for (int i = 0; i <= 59; i++){
			segundos.add(new Integer(i));
		}
		model.addAttribute("dias", dias);
		model.addAttribute("meses", meses);
		model.addAttribute("anyos", anyos);
		model.addAttribute("horas", horas);
		model.addAttribute("minutos", minutos);
		model.addAttribute("segundos", segundos);


		return "creadorEditorAvisos";
	}

	@RequestMapping(value="/crear", method = RequestMethod.POST)	
	public String procesarNuevoAviso(@ModelAttribute("aviso") Aviso aviso, BindingResult result, HttpServletRequest request) {
//		System.out.println("GestorAvisosController");
//		System.out.print(aviso);
		if(result.hasErrors()) {
			return "gestorAvisos";
		}
		String[] suppressedFields = result.getSuppressedFields();
		if (suppressedFields.length > 0) {
			throw new RuntimeException("Attempting to bind disallowed fields: " + StringUtils.arrayToCommaDelimitedString(suppressedFields));
		}


		MultipartFile archivoAdjunto = aviso.getAdjunto();
		
		String nuevoNombre = aviso.getPostInternalId() + archivoAdjunto.getOriginalFilename();


		String rootDirectory = request.getSession().getServletContext().getRealPath("/");

		if (archivoAdjunto!=null && !archivoAdjunto.isEmpty()) {
			try {
				archivoAdjunto.transferTo(new File(rootDirectory+"/resources/archivosAdjuntos/"+ nuevoNombre));
				System.out.println("Se ha guardado el archivo en : " + rootDirectory+"resources/archivosAdjuntos/"+ nuevoNombre);
			} catch (Exception e) {
				throw new RuntimeException("Product Image saving failed",e);
			}
		}

		
		
		
		//Formateado de fecha
		Date fechaCreacion = new Date(System.currentTimeMillis());
		aviso.setFechaCreacion(fechaCreacion);
//		System.out.println(fechaCreacion);

		//Se combinan los datos individuales (no mapeadosa la bd) 
		// para crear el campo definitivo de tipo Date
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		String dateInString = "" + aviso.getDia();
		dateInString += "-" + aviso.getMes();
		dateInString += "-" + aviso.getAnyo();
		dateInString += " ";
		dateInString += aviso.getHora();
		dateInString += ":" + aviso.getMinuto();
		dateInString += ":" + aviso.getSegundo();
		Date date = new Date();
//		System.out.println("Dia de hoy:" + date);
		try {
			date = sdf.parse(dateInString);
			aviso.setFechaPublicacion(date);			
		}
		catch(ParseException e){
			System.out.println("Algo fue mal");
		}

		
//		System.out.println(date); //DEBUG


		avisoService.addAviso(aviso);
		return "redirect:/avisos/ver";
	}

	@RequestMapping(value="/editar", method = RequestMethod.GET)
	public String editarAviso(@ModelAttribute("aviso") Aviso aviso, @RequestParam("id") String avisoID,Model model){		
		model.addAttribute("aviso",avisoService.getAvisoById(avisoID));
		return "creadorEditorAvisos";
	}


	@RequestMapping(value="/editar", method = RequestMethod.POST)
	public String guardarEdicionAviso(@ModelAttribute("aviso") Aviso aviso, Model model){	
//		System.out.println("GestorAvisosController---guardarEdicionAviso");
		avisoService.addAviso(aviso);		
		return "redirect:/avisos/gestor";
	}

	@InitBinder
	public void initialiseBinder(WebDataBinder binder) {
		binder.setDisallowedFields("fechaCreacion");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class,
				new CustomDateEditor(dateFormat, false));	
	}
}
