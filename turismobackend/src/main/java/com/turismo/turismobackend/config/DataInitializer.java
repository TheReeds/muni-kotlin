package com.turismo.turismobackend.config;

import com.turismo.turismobackend.dto.request.CategoriaRequest;
import com.turismo.turismobackend.dto.request.EmprendedorRequest;
import com.turismo.turismobackend.dto.request.MunicipalidadRequest;
import com.turismo.turismobackend.dto.request.RegisterRequest;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.UsuarioRepository;
import com.turismo.turismobackend.service.AuthService;
import com.turismo.turismobackend.service.CategoriaService;
import com.turismo.turismobackend.service.EmprendedorService;
import com.turismo.turismobackend.service.MunicipalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final MunicipalidadService municipalidadService;
    private final EmprendedorService emprendedorService;
    private final CategoriaService categoriaService;

    @Override
    public void run(String... args) {
        // 1. Inicializar los roles en la base de datos
        authService.initRoles();

        // 2. Verificar si ya existen usuarios para no duplicar datos
        if (usuarioRepository.count() > 0) {
            System.out.println("La base de datos ya ha sido inicializada. Omitiendo la creación de datos de demostración.");
            return;
        }

        System.out.println("Iniciando la creación de datos de demostración...");

        // 3. Crear usuario administrador (debe ser el primero)
        createAdminUser();

        // 4. Crear categorías principales (requiere autenticación de admin)
        createCategories();

        // 5. Crear usuarios de tipo Municipalidad
        var muniLimaResponse = createMunicipalidadUser("Municipalidad", "Lima", "muni_lima", "municipalidad.lima@ejemplo.com", "muni123");
        var muniCuscoResponse = createMunicipalidadUser("Municipalidad", "Cusco", "muni_cusco", "municipalidad.cusco@ejemplo.com", "muni123");
        var muniArequipaResponse = createMunicipalidadUser("Municipalidad", "Arequipa", "muni_arequipa", "municipalidad.arequipa@ejemplo.com", "muni123");
        var muniPiuraResponse = createMunicipalidadUser("Municipalidad", "Piura", "muni_piura", "municipalidad.piura@ejemplo.com", "muni123");
        var muniTrujilloResponse = createMunicipalidadUser("Municipalidad", "Trujillo", "muni_trujillo", "municipalidad.trujillo@ejemplo.com", "muni123");

        // 6. Crear usuarios de tipo Emprendedor
        var empJuanResponse = createEmprendedorUser("Juan", "Pérez", "juan_perez", "juan@ejemplo.com", "emp123");
        var empMariaResponse = createEmprendedorUser("María", "López", "maria_lopez", "maria@ejemplo.com", "emp123");
        var empCarlosResponse = createEmprendedorUser("Carlos", "Rodríguez", "carlos_rodriguez", "carlos@ejemplo.com", "emp123");
        var empLuisaResponse = createEmprendedorUser("Luisa", "García", "luisa_garcia", "luisa@ejemplo.com", "emp123");
        var empPedroResponse = createEmprendedorUser("Pedro", "Sánchez", "pedro_sanchez", "pedro@ejemplo.com", "emp123");
        var empSofiaResponse = createEmprendedorUser("Sofía", "Martínez", "sofia_martinez", "sofia@ejemplo.com", "emp123");
        var empDiegoResponse = createEmprendedorUser("Diego", "Torres", "diego_torres", "diego@ejemplo.com", "emp123");

        // 7. Crear perfiles para las municipalidades (requiere autenticación de municipalidad)
        createMunicipalidadProfile(muniLimaResponse.getId(), "Municipalidad de Lima", "Lima", "Lima", "Lima",
                "Av. Principal 123", "01-123456", "www.munilima.gob.pe",
                "La Municipalidad de Lima es la institución encargada de la gestión pública de la ciudad de Lima.");

        createMunicipalidadProfile(muniCuscoResponse.getId(), "Municipalidad de Cusco", "Cusco", "Cusco", "Cusco",
                "Plaza de Armas s/n", "084-234567", "www.municusco.gob.pe",
                "La Municipalidad de Cusco está comprometida con el desarrollo turístico y cultural de la ciudad.");

        createMunicipalidadProfile(muniArequipaResponse.getId(), "Municipalidad Provincial de Arequipa", "Arequipa", "Arequipa", "Arequipa",
                "Portal Municipal 110", "054-380050", "www.muniarequipa.gob.pe",
                "La Municipalidad Provincial de Arequipa impulsa el desarrollo sostenible y el turismo en la Ciudad Blanca.");

        createMunicipalidadProfile(muniPiuraResponse.getId(), "Municipalidad Provincial de Piura", "Piura", "Piura", "Piura",
                "Jr. Ayacucho 377", "073-284600", "www.munipiura.gob.pe",
                "La Municipalidad Provincial de Piura promueve el desarrollo turístico y gastronómico en la región norte del país.");

        createMunicipalidadProfile(muniTrujilloResponse.getId(), "Municipalidad Provincial de Trujillo", "La Libertad", "Trujillo", "Trujillo",
                "Jr. Pizarro 412", "044-246941", "www.munitrujillo.gob.pe",
                "La Municipalidad Provincial de Trujillo trabaja por el desarrollo sostenible y la promoción del turismo cultural.");

        // 8. Crear perfiles para los emprendedores (requiere autenticación de emprendedor)
        createEmprendedorProfile(empJuanResponse.getId(), "Café Peruano", "Gastronomía",
                "Jr. Comercio 345, Lima", "01-987654", "cafeperu@ejemplo.com", "www.cafeperu.com",
                "Café de especialidad con granos seleccionados de diversas regiones del Perú.",
                "Café orgánico, postres artesanales, bebidas frías", "Barismo, catas de café", 1L, 1L); // Municipalidad de Lima, Categoria Gastronomía

        createEmprendedorProfile(empMariaResponse.getId(), "Artesanías Cusco", "Artesanía",
                "Calle Plateros 123, Cusco", "084-765432", "artesanias@ejemplo.com", "www.artesaniascusco.com",
                "Taller de artesanías tradicionales cusqueñas elaboradas por artesanos locales.",
                "Tejidos, cerámicas, joyería de plata", "Talleres de tejido, visitas guiadas", 2L, 2L); // Municipalidad de Cusco, Categoria Artesanía

        createEmprendedorProfile(empCarlosResponse.getId(), "Ecoturismo Amazónico", "Turismo Ecológico",
                "Av. La Marina 456, Iquitos", "065-234567", "ecoturismo@ejemplo.com", "www.ecoturismoamazonico.com",
                "Empresa dedicada al turismo sostenible y respetuoso con el medio ambiente en la Amazonía peruana.",
                "Paquetes turísticos, souvenirs ecológicos", "Tours guiados, expediciones fotográficas, avistamiento de fauna", 1L, 3L); // Id Municipalidad inventado, debería ser uno real. Usamos Lima por ahora. Categoria Turismo Ecológico.

        createEmprendedorProfile(empLuisaResponse.getId(), "Sabores Arequipeños", "Gastronomía",
                "Calle Santa Catalina 678, Arequipa", "054-345678", "sabores@ejemplo.com", "www.saboresarequipenos.com",
                "Restaurante especializado en la auténtica gastronomía arequipeña con ingredientes locales.",
                "Rocoto relleno, chupe de camarones, queso helado", "Clases de cocina, degustaciones", 3L, 1L); // Municipalidad de Arequipa, Categoria Gastronomía

        createEmprendedorProfile(empPedroResponse.getId(), "Aventura Andina", "Turismo de Aventura",
                "Av. Sol 789, Cusco", "084-876543", "aventura@ejemplo.com", "www.aventuraandina.com",
                "Operador turístico especializado en deportes de aventura y trekking en la región andina.",
                "Equipos de montaña, indumentaria técnica", "Trekking, montañismo, ciclismo de montaña", 2L, 8L); // Municipalidad de Cusco, Categoria Deportes de Aventura

        createEmprendedorProfile(empSofiaResponse.getId(), "Cerámica Chulucanas", "Artesanía",
                "Jr. Grau 234, Piura", "073-654321", "ceramica@ejemplo.com", "www.ceramicachulucanas.com",
                "Taller artesanal que preserva y promueve la tradicional cerámica de Chulucanas.",
                "Cerámicas decorativas, jarrones, esculturas", "Demostraciones de técnicas ancestrales, talleres", 4L, 2L); // Municipalidad de Piura, Categoria Artesanía

        createEmprendedorProfile(empDiegoResponse.getId(), "Marinera Tours", "Turismo Cultural",
                "Av. España 567, Trujillo", "044-789012", "marinera@ejemplo.com", "www.marineratours.com",
                "Empresa dedicada a promover la cultura y tradiciones de la costa norte, especialmente la marinera.",
                "Souvenirs culturales, vestuario típico", "Clases de marinera, tours culturales, visitas a sitios arqueológicos", 5L, 4L); // Municipalidad de Trujillo, Categoria Turismo Cultural
        
        // 9. Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();
        System.out.println("La inicialización de datos de demostración ha finalizado correctamente.");
    }

    private void createAdminUser() {
        RegisterRequest adminRequest = RegisterRequest.builder()
                .nombre("Admin")
                .apellido("Sistema")
                .username("admin")
                .email("admin@sistema.com")
                .password("admin123")
                .roles(Collections.singleton("admin"))
                .build();
        authService.register(adminRequest);
    }

    private void createCategories() {
        createCategoryWithAuth("Gastronomía", "Restaurantes, cafeterías, cocina tradicional y especialidades locales");
        createCategoryWithAuth("Artesanía", "Productos artesanales, tejidos, cerámicas, joyería y arte tradicional");
        createCategoryWithAuth("Turismo Ecológico", "Ecoturismo, aventura, turismo sostenible y actividades al aire libre");
        createCategoryWithAuth("Turismo Cultural", "Tours culturales, sitios históricos, danzas tradicionales y patrimonio");
        createCategoryWithAuth("Alojamiento", "Hoteles, hostales, casas rurales y hospedajes comunitarios");
        createCategoryWithAuth("Transporte", "Servicios de transporte turístico, tours en vehículo y movilidad");
        createCategoryWithAuth("Wellness y Spa", "Servicios de relajación, spa, tratamientos naturales y bienestar");
        createCategoryWithAuth("Deportes de Aventura", "Trekking, montañismo, deportes extremos y aventura");
    }

    private com.turismo.turismobackend.dto.response.AuthResponse createMunicipalidadUser(
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

    private com.turismo.turismobackend.dto.response.AuthResponse createEmprendedorUser(
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

    private void createMunicipalidadProfile(Long userId, String nombre, String departamento, String provincia,
                                           String distrito, String direccion, String telefono,
                                           String sitioWeb, String descripcion) {
        authenticateAs(userId, "ROLE_MUNICIPALIDAD");

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

    private void createEmprendedorProfile(Long userId, String nombreEmpresa, String rubro, String direccion,
                                         String telefono, String email, String sitioWeb,
                                         String descripcion, String productos, String servicios,
                                         Long municipalidadId, Long categoriaId) {
        authenticateAs(userId, "ROLE_EMPRENDEDOR");

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
                .categoriaId(categoriaId)
                .build();
        emprendedorService.createEmprendedor(request);
    }

    private void createCategoryWithAuth(String nombre, String descripcion) {
        // Configurar autenticación temporal como admin para crear categorías
        Usuario adminUser = usuarioRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("El usuario 'admin' es necesario para crear categorías y no fue encontrado."));
        
        authenticateAs(adminUser.getId(), "ROLE_ADMIN");

        CategoriaRequest request = CategoriaRequest.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .build();

        try {
            categoriaService.createCategoria(request);
        } catch (Exception e) {
            // Esto podría pasar si la lógica de negocio impide duplicados, lo cual es correcto.
            System.out.println("No se pudo crear la categoría '" + nombre + "'. Posiblemente ya existe. Mensaje: " + e.getMessage());
        }
    }

    private void authenticateAs(Long userId, String role) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                usuario, null, List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}