package nlp;

import java.io.File;
import java.util.Scanner;



/**

 * Created by mmetz on 8/3/2018.

 */

public class CLIUtil {

    public static void printBanner(String title){

        printBanner(title,"");

    }

    /**

     *

     * @param title - the main title of the menu

     * @param subTitle - the subtitle of the menu

     */

    public static void printBanner(String title, String subTitle){

        //determine header border lengths based off what ever is longer title or subtitle

        int borderLength = title.length() < subTitle.length() ? subTitle.length() : title.length();

        borderLength += 2;

        //print menu header

        for(int i = 0; i < borderLength; i++)

            System.out.print("-");

        System.out.println();

        System.out.println(title);

        System.out.println(subTitle);

        for(int i = 0; i < borderLength; i++)

            System.out.print("-");

        System.out.println();

    }

    /**

     * Reusable menu interface that reprompts menu on invalid user input

     * the number returned is the indicates the index of the menuOption

     *

     * @param menuOptions -  the option that you want the menu to display

     * @return index of the option that was chosen i n menuOptions

     */

    public static int displayMenu(String [] menuOptions){

        Scanner keyboard = new Scanner(System.in);

        boolean invalidInput = false;

        int choice;

        String input;



        //keep displaying menu until we receive a valid option input

        while (true)

        {

            //print menu options

            for(int i = 0; i < menuOptions.length; i++)

                System.out.println(i + ") " + menuOptions[i]);

            System.out.println();



            //prompt for user input

            System.out.print("Enter ");

            for(int i = 0; i < menuOptions.length; i++)

                System.out.print("\"" + i + "\", ");

            input = keyboard.nextLine();



            //validate if it is a valid option the user entered

            try {

                choice = Integer.parseInt(input);

                if (choice < 0 || (menuOptions.length - 1) < choice)

                    throw new Exception();

                break;

            }catch (Exception e){

                System.out.println("\"" + input + "\"" + " is not an option, please try again");

                continue;

            }



        }



        return choice;

    }





    /**

     * Utility to display a banner message to the console

     * @param bannerText

     */

    public static void printJumboBanner(String bannerText) {

        for(int i = 0; i < bannerText.length() + 4; i++)

            System.out.print("#");

        System.out.println();

        for(int i = 0; i < bannerText.length() + 4; i++) {

            if(i == 0 || i == bannerText.length() + 4 - 1)

                System.out.print("#");

            else

                System.out.print(" ");

        }

        System.out.println();

        System.out.println("# " + bannerText + " #");

        for(int i = 0; i < bannerText.length() + 4; i++) {

            if(i == 0 || i == bannerText.length() + 4 - 1)

                System.out.print("#");

            else

                System.out.print(" ");

        }

        System.out.println();

        for(int i = 0; i < bannerText.length() + 4; i++)

            System.out.print("#");

        System.out.println();

    }



    public static void printFileSaveSuccessMessage(File file){

        System.out.println("Success your file has been saved to: " + file.getAbsolutePath());

    }



    public static void printFileSaveFailMessage(File file) {

        System.out.println("Failed your file could not be saved to: " + file.getAbsolutePath());



    }

}