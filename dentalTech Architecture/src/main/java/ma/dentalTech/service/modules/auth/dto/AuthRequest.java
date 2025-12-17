package ma.dentalTech.service.modules.auth.dto;

public record AuthRequest(
        String login,
        String password
) {}
