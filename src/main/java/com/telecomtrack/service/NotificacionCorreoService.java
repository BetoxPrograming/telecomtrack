package com.telecomtrack.service;

import com.telecomtrack.domain.Material;
import com.telecomtrack.domain.Solicitud;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionCorreoService {

    private static final String ROL_ADMINISTRADOR = "Administrador";
    private static final String ROL_BODEGUERO = "Bodeguero";

    private final ConfiguracionNotificacionService configuracionNotificacionService;
    private final CorreoService correoService;
    private final UsuarioRepository usuarioRepository;

    public NotificacionCorreoService(
            ConfiguracionNotificacionService configuracionNotificacionService,
            CorreoService correoService,
            UsuarioRepository usuarioRepository) {
        this.configuracionNotificacionService = configuracionNotificacionService;
        this.correoService = correoService;
        this.usuarioRepository = usuarioRepository;
    }

    public void notificarNuevaSolicitud(Solicitud solicitud) {
        if (!configuracionNotificacionService.notificarSolicitudesPendientes()
                || !correoService.estaConfigurado()) {
            return;
        }

        List<Usuario> bodegueros = usuarioRepository
                .findByActivoTrueAndRolOrderByNombreAsc(ROL_BODEGUERO);

        String asunto = "TelecomTrack - Nueva solicitud pendiente #" + solicitud.getIdSolicitud();
        String contenido = """
                <h2>Nueva solicitud pendiente</h2>
                <p>Se registró una nueva solicitud que requiere revisión de Bodega.</p>
                <p><strong>Solicitud:</strong> #%s</p>
                <p><strong>Técnico:</strong> %s %s</p>
                <p><strong>Proyecto:</strong> %s</p>
                <p><strong>Ubicación:</strong> %s</p>
                <p>Ingrese a TelecomTrack para revisar y aprobar o rechazar la solicitud.</p>
                """.formatted(
                solicitud.getIdSolicitud(),
                solicitud.getTecnico().getNombre(),
                solicitud.getTecnico().getApellido(),
                solicitud.getProyecto().getNombre(),
                solicitud.getUbicacion().getNombre());

        enviarSinInterrumpirOperacion(bodegueros, asunto, contenido);
    }

    public void notificarStockMinimo(Material material) {
        if (!configuracionNotificacionService.notificarStockMinimo()
                || !correoService.estaConfigurado()
                || !material.isStockBajo()) {
            return;
        }

        List<Usuario> administradores = usuarioRepository
                .findByActivoTrueAndRolOrderByNombreAsc(ROL_ADMINISTRADOR);

        String ubicacion = material.getUbicacion() != null
                ? material.getUbicacion().getNombre()
                : "Sin ubicación asignada";

        String asunto = "TelecomTrack - Alerta de stock mínimo: " + material.getNombre();
        String contenido = """
                <h2>Alerta de stock mínimo</h2>
                <p>Un material alcanzó o quedó por debajo de su stock mínimo.</p>
                <p><strong>Código:</strong> %s</p>
                <p><strong>Material:</strong> %s</p>
                <p><strong>Stock actual:</strong> %s %s</p>
                <p><strong>Stock mínimo:</strong> %s %s</p>
                <p><strong>Ubicación:</strong> %s</p>
                <p>Ingrese a TelecomTrack para revisar el inventario.</p>
                """.formatted(
                material.getCodigoUnico(),
                material.getNombre(),
                material.getStockActual(),
                material.getUnidadMedida(),
                material.getStockMinimo(),
                material.getUnidadMedida(),
                ubicacion);

        enviarSinInterrumpirOperacion(administradores, asunto, contenido);
    }

    private void enviarSinInterrumpirOperacion(
            List<Usuario> destinatarios,
            String asunto,
            String contenido) {

        for (Usuario usuario : destinatarios) {
            try {
                correoService.enviarCorreoHtml(usuario.getCorreo(), asunto, contenido);
            } catch (Exception ignored) {
                // El correo es complementario: una falla SMTP no debe revertir
                // la solicitud ni el movimiento de inventario que ya se procesó.
            }
        }
    }
}
