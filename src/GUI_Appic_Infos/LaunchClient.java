package GUI_Appic_Infos;

import java.io.IOException;

public class LaunchClient
{
    public static void main(String[] args) {

        try {
            new GUI_Applic_Infos();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
