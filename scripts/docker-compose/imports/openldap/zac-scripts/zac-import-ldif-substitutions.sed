# SED script file with template substitutions used by the ZAC LDAP LDIF import script
s/\${TESTUSER1_EMAIL_ADDRESS}/${ZAC_TESTUSER1_EMAIL_ADDRESS}/g
s/\${TESTUSER2_EMAIL_ADDRESS}/${ZAC_TESTUSER2_EMAIL_ADDRESS}/g
s/\${RECORDMANAGER1_EMAIL_ADDRESS}/${ZAC_RECORD_MANAGER_1_EMAIL_ADDRESS}/g
s/\${FUNCTIONAL_ADMIN1_EMAIL_ADDRESS}/${ZAC_FUNCTIONAL_ADMIN_1_EMAIL_ADDRESS}/g
s/\${GROUP_A_EMAIL_ADDRESS}/${ZAC_GROUP_A_EMAIL_ADDRESS}/g
s/\${GROUP_FUNCTIONEEL_BEHEERDERS_EMAIL_ADDRESS}/${ZAC_GROUP_FUNCTIONEEL_BEHEERDERS_EMAIL_ADDRESS}/g
s/\${GROUP_RECORD_MANAGERS_EMAIL_ADDRESS}/${ZAC_GROUP_RECORD_MANAGERS_EMAIL_ADDRESS}/g