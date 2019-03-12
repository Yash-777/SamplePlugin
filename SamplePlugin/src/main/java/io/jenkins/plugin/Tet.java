package io.jenkins.plugin;

public class Tet {
	public static void main(String[] args) {
		String s = "Hi";
		String s2 = s.toUpperCase();
		if(s.equals(s2)) {
			System.out.println(true);
		} else {
			System.out.println(false);
		}
	}
}
