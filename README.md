# Paint shop problem
I'm Edgar Rubio, the source code in this reposiroty is my solution to the "Paint Shop Problem", to read the problem description click the next link

[Problem description](PROBLEM.md)

## Running the code

### Requirements

If you want to execute the code you will Maven (version >= 3.5.3) and Java (version >= 1.5)

### Build the application
Use the next command to compile and build the application:
```bash
mvn clean install
```

### Running

The solution is constructed as a command line program that accepts the path to the input file as first parameter (and the only one).

The command can be executed with:

```bash
java -jar <path_to_jar_file>/paintshop-1.0.jar <path_to_input_file>/<FileDataSet.txt>
```

The sample solution that comes with the problem can be found at [`examples`](examples) for test purposes.

## The solution algorithm

In this section is described the solution algorithm I designed and implemented to solve the problem.

The general idea is to find all the possible combinations and test them all to guarantee compliance with the constraints. The valid combinations are tested again to find the one with the less "matte" colors. The solution is stored to proceed with the next case until all of them are done.

There are some special cases listed below: 
  - If during the evaluation process a valid combination has only "glossy" colors, it is taken inmediatly without exploring other possible solutions for that case. 
  - If a customer wants two "matte" colors the case is marked as **"IMPOSSIBLE"**, because as the instructions says **"At most one of the types a customer likes will be a "matte""**.
  - Another special case is if all customers want glossy colors, the case solution is marked with all colors glossy without further evaluation.

### Step 1 — Input parsing

In this step the source file is parsed and sotored in a bidimensional array, taking all the necessary spaces for each customer to store his liked colors.

The parser looks for customers who wnat more than 1 Matte color and for cases where all the customers want glossy colors.

At the end of the parsing we have the array full with the input data and we also know if a customer wants more than 1 matte color or if all customers want glossy colors.
