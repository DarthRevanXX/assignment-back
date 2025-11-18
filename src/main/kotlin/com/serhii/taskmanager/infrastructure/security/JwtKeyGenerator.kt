package com.serhii.taskmanager.infrastructure.security

import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.security.KeyPairGenerator
import java.util.Base64

/**
 * Generates RSA key pair at startup if not present.
 * Keys are stored in /tmp for demo purposes (not persisted between restarts).
 * In production, use proper secret management (Vault, K8s secrets, etc.)
 */
@Startup
@ApplicationScoped
class JwtKeyGenerator(
  @param:ConfigProperty(name = "jwt.keys.directory", defaultValue = "/tmp/task-manager-keys")
  private val keysDirectory: String,
) {
  private val privateKeyPath: Path = Paths.get(keysDirectory, "privateKey.pem")
  private val publicKeyPath: Path = Paths.get(keysDirectory, "publicKey.pem")

  init {
    ensureKeysExist()
  }

  private fun ensureKeysExist() {
    val dir = Paths.get(keysDirectory)
    if (!Files.exists(dir)) {
      Files.createDirectories(dir)
      Log.info("Created JWT keys directory: $keysDirectory")
    }

    if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
      Log.info("JWT keys already exist, using existing keys")
      return
    }

    Log.info("Generating new RSA key pair for JWT signing...")
    val keyPair =
      KeyPairGenerator
        .getInstance("RSA")
        .apply {
          initialize(2048)
        }.generateKeyPair()

    val privateKeyPem =
      buildString {
        append("-----BEGIN PRIVATE KEY-----\n")
        append(Base64.getMimeEncoder(64, "\n".toByteArray()).encodeToString(keyPair.private.encoded))
        append("\n-----END PRIVATE KEY-----\n")
      }
    Files.writeString(privateKeyPath, privateKeyPem, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    Files.setPosixFilePermissions(
      privateKeyPath,
      setOf(
        java.nio.file.attribute.PosixFilePermission.OWNER_READ,
        java.nio.file.attribute.PosixFilePermission.OWNER_WRITE,
      ),
    )

    val publicKeyPem =
      buildString {
        append("-----BEGIN PUBLIC KEY-----\n")
        append(Base64.getMimeEncoder(64, "\n".toByteArray()).encodeToString(keyPair.public.encoded))
        append("\n-----END PUBLIC KEY-----\n")
      }
    Files.writeString(publicKeyPath, publicKeyPem, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

    Log.info("Generated new RSA key pair at: $keysDirectory")
    Log.warn("Keys stored in /tmp - will be regenerated on restart (demo mode)")
  }
}
