package similarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import net.barenca.jastyle.ASFormatter;
import net.barenca.jastyle.FormatterHelper;

public class LCS {

	ArrayList<String> seq1;
	ArrayList<String> seq2;
	int matrix[][];

	public LCS(ArrayList<String> code1, ArrayList<String> code2) {
		// assigning array lists
		this.seq1 = new ArrayList<String>();
		this.seq1 = code1;
		this.seq2 = new ArrayList<String>();
		this.seq2 = code2;
		matrix = new int[this.seq1.size() + 1][this.seq2.size() + 1];
	}

	public ArrayList<String> getLCS(int len1, int len2) {
		// code for getting LCS
		String last1 = new String();
		String last2 = new String();

		ArrayList<String> lcs1 = new ArrayList<String>();
		ArrayList<String> lcs2 = new ArrayList<String>();

		int lenlcs1, lenlcs2;

		if (len1 <= 0 || len2 <= 0)
			return new ArrayList<String>();

		last1 = this.seq1.get(len1 - 1);
		last2 = this.seq2.get(len2 - 1);

		if (last1.equals(last2)) {
			ArrayList<String> temp = getLCS(len1 - 1, len2 - 1);
			temp.add(last1);
			return temp;
		} else {
			lcs1 = getLCS(len1, len2 - 1);
			lcs2 = getLCS(len1 - 1, len2);
			lenlcs1 = lcs1.size();
			lenlcs2 = lcs2.size();
			return lenlcs1 > lenlcs2 ? lcs1 : lcs2;
		}
	}

	public ArrayList<String> getLCS_Dynamic(int len1, int len2) {
		// code for getting LCS with dynamic programming
		ArrayList<String> mylcs = new ArrayList<String>();
		for (int i = len1 - 1; i >= 0; i--) {
			for (int j = len2 - 1; j >= 0; j--) {
				if (this.seq1.get(i).toString()
						.equals(this.seq2.get(j).toString())) {
					matrix[i][j] = matrix[i + 1][j + 1] + 1;
				} else {
					matrix[i][j] = Math.max(matrix[i + 1][j], matrix[i][j + 1]);
				}
			}
		}
		int i = 0, j = 0;
		while (i < len1 && j < len2) {
			if (this.seq1.get(i).toString().equals(this.seq2.get(j).toString())) {
				mylcs.add(this.seq1.get(i).toString());
				i++;
				j++;
			} else if (matrix[i + 1][j] >= matrix[i][j + 1])
				i++;
			else
				j++;
		}

		// returning LCS
		return mylcs;
	}

	protected static ArrayList<String> getTokenized(String code) {
		// code for getting tokens of a code fragment
		String tcode = format_the_code(code);
		String fcode = remove_code_comment(tcode);
		StringTokenizer tokenizer = new StringTokenizer(fcode);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token.trim();
			if (!token.isEmpty()) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public static void show_the_lcs(ArrayList<String> lcs) {
		for (String str : lcs)
			System.out.println(str);
		System.out.println("-----------------");
	}

	static String load_code_snippet(String fileName) {
		String codes = new String();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				codes += line + "\n";
			}
		} catch (Exception exc) {
		}
		return codes;
	}

	protected static String remove_code_comment(String codeFragment) {
		// code for removing code fragment
		String pattern = "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
		return codeFragment.replaceAll(pattern, "");

	}

	protected static String format_the_code(String codeFragment) {
		// code for formatting code fragment
		ASFormatter formatter = new ASFormatter();
		Reader in = new BufferedReader(new StringReader(codeFragment));
		formatter.setJavaStyle();
		String formattedCode = FormatterHelper.format(in, formatter);
		return formattedCode;
	}

	public static void main(String args[]) {
		/*
		 * String seq1= "s k u l l a n d b o n e s",
		 * seq2="l u l l a b y b a b i e s"; ArrayList<String>
		 * list1=getTokenized(seq1); ArrayList<String> list2=getTokenized(seq2);
		 * System.out.println(list1.size()+" "+list2.size());
		 */

		String fileName1 = "D:/My MSc/MyDataset/StackCommentData/tdata/context.txt";
		String fileName2 = "D:/My MSc/MyDataset/StackCommentData/tdata/code2.txt";

		String code1 = load_code_snippet(fileName1);
		ArrayList<String> list1 = getTokenized(code1);
		// show_the_lcs(list1);
		String code2 = load_code_snippet(fileName2);
		ArrayList<String> list2 = getTokenized(code2);
		// show_the_lcs(list2);

		System.out.println("List size:" + list1.size() + " " + list2.size());

		LCS lcs = new LCS(list1, list2);
		// ArrayList<String> mylcs = lcs.getLCS(list1.size(), list2.size());
		ArrayList<String> mylcs = lcs
				.getLCS_Dynamic(list1.size(), list2.size());
		show_the_lcs(mylcs);
		System.out.println("LCS size:" + mylcs.size());

	}
}
