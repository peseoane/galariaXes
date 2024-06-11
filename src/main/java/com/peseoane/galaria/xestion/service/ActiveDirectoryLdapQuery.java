package com.peseoane.galaria.xestion.service;


import com.peseoane.galaria.xestion.etc.InformacionOrdenador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;


@Service
public class ActiveDirectoryLdapQuery {

    private static final Logger log = LoggerFactory.getLogger(ActiveDirectoryLdapQuery.class);

    @Autowired
    private LdapTemplate ldapTemplate;

    public String getOuChain(String ou) {
    return ldapTemplate.search(query().where("ou").is(ou),
        (AttributesMapper<String>) attrs -> {
            String[] distinguishedNameParts = attrs.get("distinguishedName").get().toString().split(",");
            StringBuilder ouChain = new StringBuilder();
            for (String part : distinguishedNameParts) {
                if (part.startsWith("OU=")) {
                    ouChain.append(part.substring(3)).append(",");
                }
            }
            // Remove the trailing comma
            if (ouChain.length() > 0) {
                ouChain.setLength(ouChain.length() - 1);
            }
            return ouChain.toString();
        }).get(0);
}

    public InformacionOrdenador getOrdenadorByCn(String cn) {
        return ldapTemplate.search(query().where("cn").is(cn), (AttributesMapper<InformacionOrdenador>) attrs -> {
            String computerName = attrs.get("cn").get().toString();
            String[] distinguishedNameParts = attrs.get("distinguishedName").get().toString().split(",");
            List<String> OU = new ArrayList<>();
            String lugar = null;
            for (String part : distinguishedNameParts) {

                // remove the first OU=
                String ou = part.substring(3);
                log.info("OU: {}", ou);
                OU.add(ou);
                if (ou.contains("Vigo")) {
                    lugar = "Vigo";
                } else if (ou.contains("Santiago")) {
                    lugar = "Santiago";
                } else if (ou.contains("Coruña")) {
                    lugar = "Coruña";
                } else if (ou.contains("Ourense")) {
                    lugar = "Ourense";
                } else if (ou.contains("Pontevedra")) {
                    lugar = "Pontevedra";
                } else if (ou.contains("Lugo")) {
                    lugar = "Lugo";
                }

            }
            return new InformacionOrdenador(computerName, OU, lugar);
        }).get(0);

    }

    public List<InformacionOrdenador> getOrdenadores() {
        return ldapTemplate.search(query().where("objectclass").is("computer"),
                                   (AttributesMapper<InformacionOrdenador>) attrs -> {
                                       String computerName = attrs.get("cn").get().toString();
                                       String[] distinguishedNameParts = attrs.get("distinguishedName").get().toString()
                                               .split(",");
                                       List<String> OU = new ArrayList<>();
                                       String lugar = null;
                                       for (String part : distinguishedNameParts) {
                                           String ou = part.substring(3);
                                           OU.add(ou);
                                           if (ou.contains("Vigo")) {
                                               lugar = "Vigo";
                                           } else if (ou.contains("Santiago")) {
                                               lugar = "Santiago";
                                           } else if (ou.contains("Coruña")) {
                                               lugar = "Coruña";
                                           } else if (ou.contains("Ourense")) {
                                               lugar = "Ourense";
                                           } else if (ou.contains("Pontevedra")) {
                                               lugar = "Pontevedra";
                                           } else if (ou.contains("Lugo")) {
                                               lugar = "Lugo";
                                           }

                                       }
                                       return new InformacionOrdenador(computerName, OU, lugar);
                                   });
    }

}