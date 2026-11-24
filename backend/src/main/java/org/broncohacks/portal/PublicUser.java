package org.broncohacks.portal;

import java.util.UUID;

public record PublicUser(UUID id, String name, String email) {}
