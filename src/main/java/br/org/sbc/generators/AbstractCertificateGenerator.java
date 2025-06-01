package br.org.sbc.generators;

import br.org.sbc.model.Certificate;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.IOException;
import java.util.UUID;

public abstract class AbstractCertificateGenerator {
    protected Certificate certificate;
    protected String outputPrefix;
    protected float fontSize;
    protected float positionY;

    protected void applyReadOnlyPermissions() {
        AccessPermission ap = new AccessPermission();
        ap.setCanPrint(true);                               // allow printing
        ap.setCanModify(false);                             // prevent modification
        ap.setCanExtractContent(false);                     // prevent copying/extracting

        String password = UUID.randomUUID().toString();     // random password
        StandardProtectionPolicy spp = new StandardProtectionPolicy(password, null, ap);
        try {
            certificate.getDocument().protect(spp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
