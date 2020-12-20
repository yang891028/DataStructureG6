import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Enter a book name");
		Scanner in=new Scanner(System.in);
		String str=in.nextLine();
		str=str.replaceAll("\\s","+");
		System.out.println(str);
		try {
			System.out.println(new GoogleQuery(str).query());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
