import java.io.IOException;
import java.util.ArrayList;

public class Exam {

	private String id;
	private ArrayList<String> students = new ArrayList<String>();
	private int number_st_enr;

	public Exam(String id, int number_st_enr) {
		this.id = id;
		this.number_st_enr = number_st_enr;
	}

	public String getID() {
		return this.id;
	}

	public void setStudents(ArrayList<String> students) {
		this.students = students;
	}

	public ArrayList<String> getStudents() {
		return this.students;
	}

	public void addStudent(String s) throws IOException {
		students.add(s);
		if (this.students.size() > this.number_st_enr)
			throw new IOException();
	}

	public void setNumber_st_enr(int number_st_enr) {
		this.number_st_enr = number_st_enr;
	}

	public int getNumber_st_enr() {
		return this.number_st_enr;
	}

	public String toString() {
		return this.id + ", " + this.number_st_enr;
	}

	
}
