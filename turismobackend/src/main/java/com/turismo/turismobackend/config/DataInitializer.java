package com.turismo.turismobackend.config;

import com.turismo.turismobackend.dto.request.EmprendedorRequest;
import com.turismo.turismobackend.dto.request.MunicipalidadRequest;
import com.turismo.turismobackend.dto.request.RegisterRequest;
import com.turismo.turismobackend.model.Rol;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.UsuarioRepository;
import com.turismo.turismobackend.service.AuthService;
import com.turismo.turismobackend.service.EmprendedorService;
import com.turismo.turismobackend.service.MunicipalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final MunicipalidadService municipalidadService;
    private final EmprendedorService emprendedorService;
    
    @Override
    public void run(String... args) {
        // Inicializar los roles en la base de datos
        authService.initRoles();
        
        // Crear usuarios demo si no existen
        createDemoUsersIfNotExist();
    }
    
    private void createDemoUsersIfNotExist() {
        // Verificar si ya existen usuarios en el sistema
        if (usuarioRepository.count() > 0) {
            return; // No crear usuarios si ya existen
        }
        
        // 1. Crear usuario administrador
        RegisterRequest adminRequest = RegisterRequest.builder()
                .nombre("Admin")
                .apellido("Sistema")
                .username("admin")
                .email("admin@sistema.com")
                .password("admin123")
                .roles(Collections.singleton("admin"))
                .build();
        
        authService.register(adminRequest);
        
        // 2. Crear usuarios municipalidad (agregamos más municipalidades)
        var muniLimaResponse = createMunicipalidad("Municipalidad", "Lima", "muni_lima", "municipalidad.lima@ejemplo.com", "muni123");
        var muniCuscoResponse = createMunicipalidad("Municipalidad", "Cusco", "muni_cusco", "municipalidad.cusco@ejemplo.com", "muni123");
        var muniArequipaResponse = createMunicipalidad("Municipalidad", "Arequipa", "muni_arequipa", "municipalidad.arequipa@ejemplo.com", "muni123");
        var muniPiuraResponse = createMunicipalidad("Municipalidad", "Piura", "muni_piura", "municipalidad.piura@ejemplo.com", "muni123");
        var muniTrujilloResponse = createMunicipalidad("Municipalidad", "Trujillo", "muni_trujillo", "municipalidad.trujillo@ejemplo.com", "muni123");
        
        // 3. Crear usuarios emprendedores (agregamos más emprendedores)
        var empJuanResponse = createEmprendedor("Juan", "Pérez", "juan_perez", "juan@ejemplo.com", "emp123");
        var empMariaResponse = createEmprendedor("María", "López", "maria_lopez", "maria@ejemplo.com", "emp123");
        var empCarlosResponse = createEmprendedor("Carlos", "Rodríguez", "carlos_rodriguez", "carlos@ejemplo.com", "emp123");
        var empLuisaResponse = createEmprendedor("Luisa", "García", "luisa_garcia", "luisa@ejemplo.com", "emp123");
        var empPedroResponse = createEmprendedor("Pedro", "Sánchez", "pedro_sanchez", "pedro@ejemplo.com", "emp123");
        var empSofiaResponse = createEmprendedor("Sofía", "Martínez", "sofia_martinez", "sofia@ejemplo.com", "emp123");
        var empDiegoResponse = createEmprendedor("Diego", "Torres", "diego_torres", "diego@ejemplo.com", "emp123");
        
        // 4. Crear perfiles de municipalidades
        createMunicipalidadWithAuth(muniLimaResponse.getId(), "Municipalidad de Lima", "Lima", "Lima", "Lima", 
                "Av. Principal 123", "01-123456", "www.munilima.gob.pe", 
                "La Municipalidad de Lima es la institución encargada de la gestión pública de la ciudad de Lima.");
        
        createMunicipalidadWithAuth(muniCuscoResponse.getId(), "Municipalidad de Cusco", "Cusco", "Cusco", "Cusco", 
                "Plaza de Armas s/n", "084-234567", "www.municusco.gob.pe", 
                "La Municipalidad de Cusco está comprometida con el desarrollo turístico y cultural de la ciudad.");
        
        createMunicipalidadWithAuth(muniArequipaResponse.getId(), "Municipalidad Provincial de Arequipa", "Arequipa", "Arequipa", "Arequipa", 
                "Portal Municipal 110", "054-380050", "www.muniarequipa.gob.pe", 
                "La Municipalidad Provincial de Arequipa impulsa el desarrollo sostenible y el turismo en la Ciudad Blanca.");
        
        createMunicipalidadWithAuth(muniPiuraResponse.getId(), "Municipalidad Provincial de Piura", "Piura", "Piura", "Piura", 
                "Jr. Ayacucho 377", "073-284600", "www.munipiura.gob.pe", 
                "La Municipalidad Provincial de Piura promueve el desarrollo turístico y gastronómico en la región norte del país.");
        
        createMunicipalidadWithAuth(muniTrujilloResponse.getId(), "Municipalidad Provincial de Trujillo", "La Libertad", "Trujillo", "Trujillo", 
                "Jr. Pizarro 412", "044-246941", "www.munitrujillo.gob.pe", 
                "La Municipalidad Provincial de Trujillo trabaja por el desarrollo sostenible y la promoción del turismo cultural.");
        
        // 5. Crear perfiles de emprendedores
        createEmprendedorWithAuth(empJuanResponse.getId(), "Café Peruano", "Gastronomía", 
                "Jr. Comercio 345, Lima", "01-987654", "cafeperu@ejemplo.com", "www.cafeperu.com",
                "Café de especialidad con granos seleccionados de diversas regiones del Perú.",
                "Café orgánico, postres artesanales, bebidas frías", "Barismo, catas de café", 1L);
        
        createEmprendedorWithAuth(empMariaResponse.getId(), "Artesanías Cusco", "Artesanía", 
                "Calle Plateros 123, Cusco", "084-765432", "artesanias@ejemplo.com", "www.artesaniascusco.com",
                "Taller de artesanías tradicionales cusqueñas elaboradas por artesanos locales.",
                "Tejidos, cerámicas, joyería de plata", "Talleres de tejido, visitas guiadas", 2L);
        
        createEmprendedorWithAuth(empCarlosResponse.getId(), "Ecoturismo Amazónico", "Turismo Ecológico", 
                "Av. La Marina 456, Iquitos", "065-234567", "ecoturismo@ejemplo.com", "www.ecoturismoamazonico.com",
                "Empresa dedicada al turismo sostenible y respetuoso con el medio ambiente en la Amazonía peruana.",
                "Paquetes turísticos, souvenirs ecológicos", "Tours guiados, expediciones fotográficas, avistamiento de fauna", 1L);
        
        createEmprendedorWithAuth(empLuisaResponse.getId(), "Sabores Arequipeños", "Gastronomía", 
                "Calle Santa Catalina 678, Arequipa", "054-345678", "sabores@ejemplo.com", "www.saboresarequipenos.com",
                "Restaurante especializado en la auténtica gastronomía arequipeña con ingredientes locales.",
                "Rocoto relleno, chupe de camarones, queso helado", "Clases de cocina, degustaciones", 3L);
        
        createEmprendedorWithAuth(empPedroResponse.getId(), "Aventura Andina", "Turismo de Aventura", 
                "Av. Sol 789, Cusco", "084-876543", "aventura@ejemplo.com", "www.aventuraandina.com",
                "Operador turístico especializado en deportes de aventura y trekking en la región andina.",
                "Equipos de montaña, indumentaria técnica", "Trekking, montañismo, ciclismo de montaña", 2L);
        
        createEmprendedorWithAuth(empSofiaResponse.getId(), "Cerámica Chulucanas", "Artesanía", 
                "Jr. Grau 234, Piura", "073-654321", "ceramica@ejemplo.com", "www.ceramicachulucanas.com",
                "Taller artesanal que preserva y promueve la tradicional cerámica de Chulucanas.",
                "Cerámicas decorativas, jarrones, esculturas", "Demostraciones de técnicas ancestrales, talleres", 4L);
        
        createEmprendedorWithAuth(empDiegoResponse.getId(), "Marinera Tours", "Turismo Cultural", 
                "Av. España 567, Trujillo", "044-789012", "marinera@ejemplo.com", "www.marineratours.com",
                "Empresa dedicada a promover la cultura y tradiciones de la costa norte, especialmente la marinera.",
                "Souvenirs culturales, vestuario típico", "Clases de marinera, tours culturales, visitas a sitios arqueológicos", 5L);
    }
    
    private com.turismo.turismobackend.dto.response.AuthResponse createMunicipalidad(
            String nombre, String apellido, String username, String email, String password) {
        RegisterRequest request = RegisterRequest.builder()
                .nombre(nombre)
                .apellido(apellido)
                .username(username)
                .email(email)
                .password(password)
                .roles(Collections.singleton("municipalidad"))
                .build();
        
        return authService.register(request);
    }
    
    private com.turismo.turismobackend.dto.response.AuthResponse createEmprendedor(
            String nombre, String apellido, String username, String email, String password) {
        RegisterRequest request = RegisterRequest.builder()
                .nombre(nombre)
                .apellido(apellido)
                .username(username)
                .email(email)
                .password(password)
                .roles(Collections.singleton("emprendedor"))
                .build();
        
        return authService.register(request);
    }
    
    private void createMunicipalidadWithAuth(Long userId, String nombre, String departamento, String provincia, 
                                           String distrito, String direccion, String telefono, 
                                           String sitioWeb, String descripcion) {
        // Obtener usuario
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Configurar autenticación
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_MUNICIPALIDAD");
        Authentication auth = new UsernamePasswordAuthenticationToken(
                usuario, null, List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // Crear municipalidad
        MunicipalidadRequest request = MunicipalidadRequest.builder()
                .nombre(nombre)
                .departamento(departamento)
                .provincia(provincia)
                .distrito(distrito)
                .direccion(direccion)
                .telefono(telefono)
                .sitioWeb(sitioWeb)
                .descripcion(descripcion)
                .build();
        
        municipalidadService.createMunicipalidad(request);
    }
    
    private void createEmprendedorWithAuth(Long userId, String nombreEmpresa, String rubro, String direccion,
                                         String telefono, String email, String sitioWeb,
                                         String descripcion, String productos, String servicios,
                                         Long municipalidadId) {
        // Obtener usuario
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Configurar autenticación
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_EMPRENDEDOR");
        Authentication auth = new UsernamePasswordAuthenticationToken(
                usuario, null, List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // Crear emprendedor
        EmprendedorRequest request = EmprendedorRequest.builder()
                .nombreEmpresa(nombreEmpresa)
                .rubro(rubro)
                .direccion(direccion)
                .telefono(telefono)
                .email(email)
                .sitioWeb(sitioWeb)
                .descripcion(descripcion)
                .productos(productos)
                .servicios(servicios)
                .municipalidadId(municipalidadId)
                .build();
        
        emprendedorService.createEmprendedor(request);
    }
}