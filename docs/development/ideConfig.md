# IDE Configuration

## IntelliJ IDEA

### Code style settings

#### Java

1. Open your Intellij Idea settings
2. Navigate to Editor -> Code Style -> Java
3. From Scheme options select Import Scheme -> Eclipse XML Profile
![code_style_settings.png](./attachments/images/code_style_settings.png)
4. Open `./config/zac.xml` from the project root
5. Save the configuration
7. Install [Spotless Gradle plugin](https://plugins.jetbrains.com/plugin/18321-spotless-gradle)
8. Open Actions pop-up (Ctrl+⇧+A / ⇧⌘A) and select "Reformat File With Spotless"
![intellij-popup-spotless](./attachments/images/intellij-actions-popup-spotless.png)
9. Click on "Assign Shortcut" at the bottom
10. Assign the default reformat shortcut (Crtl+Alt+L / ⌘⌥L)
![intellij-reformat-keybinding](./attachments/images/intellij-reformat-keybinding.png)
11. Select "Remove" from the next Warning dialog
![intellij-remove-default-assignment](./attachments/images/intellij-remove-default-assignment.png)

### Run ZAC in IntelliJ

#### Prerequisites
* Correct JDK distribution (see [above](#prerequisites))
* [Local WildFly installation](../../scripts/wildfly/README.md).
* IntelliJ [WilldFly plugin](https://plugins.jetbrains.com/plugin/20219-wildfly) which is only available in the IntelliJ Enterprise Edition.
* The [1Password CLI extensions](https://developer.1password.com/docs/cli/)
* ZAC project imported/open in IntelliJ via 'Open as Gradle project'.

#### Setup Wildfly for ZAC
1. Enable the built-in IntelliJ WildFly extension if not already enabled (it requires the IntelliJ Enterprise Edition).
2. Create a run configuration using the WildFly extension for ZAC using the `JBoss/Wildfly Server - local` template.
   Select `Application server` - `Configure` and point it to your local Wildfly installation in the project root.
   Do not change the automatically detected libraries!
   ![zac-intellij-runtime-wildfly-1.png](./attachments/images/zac-intellij-runtime-wildfly-1.png)
3. Change the Wildfly configuration as follows:
   ![zac-intellij-runtime-wildfly-2.png](./attachments/images/zac-intellij-runtime-wildfly-2.png)
4. Add the 'exploded zaakafhandelcomponent WAR' artifact to the deployment:
   ![zac-intellij-runtime-wildfly-3.png](./attachments/images/zac-intellij-runtime-wildfly-3.png)
5. Configure 1Password CLI extensions to populate the required environment variables in Startup/Connection.
   Uncheck the `use default` for the startup script and select the `startupwithenv.sh` script from the project root.
   Next add a new env var called `APP_ENV` and set the value to `devlocal`.
   ![zac-intellij-runtime-wildfly-4.png](./attachments/images/zac-intellij-runtime-wildfly-4.png)

#### KVK integration
The KVK integration of ZAC is based on the [KVK API](https://developers.kvk.nl/).
By default, (depending on your environment variables; see below) ZAC integrates with the [KVK test environment](https://developers.kvk.nl/documentation/testing).
If you run ZAC from IntelliJ this requires a number of certificates to be added to your local Java keystore.

Please follow the instructions on: https://developers.kvk.nl/documentation/install-tls-certificate.

#### Starting up ZAC

1. To start up ZAC from IntelliJ, select the IntelliJ configuration created above and run it (normally in `Debug` mode).
2. After starting up ZAC you should see something like this in IntelliJ:
   ![zac-intellij-runtime-wildfly-5.png](./attachments/images/zac-intellij-runtime-wildfly-5.png)
3. After logging in using one of the available test users you should see the ZAC UI:
   ![zac-ui-1.png](./attachments/images/zac-ui-1.png)