# Paint shop problem
I'm Edgar Rubio, the source code in this repository is my solution to the "Paint Shop Problem", to read the problem description click the next link

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
  - If during the evaluation process a valid combination has only "glossy" colors, it is taken immediately without exploring other possible solutions for that case. 
  - If a customer wants two "matte" colors the case is marked as **"IMPOSSIBLE"**, because as the instructions says **"At most one of the types a customer likes will be a "matte""**.
  - Another special case is if all customers want glossy colors, the case solution is marked with all colors glossy without further evaluation.

### Step 1 — Input parsing

In this step the source file is parsed and stored in a bidimensional array, taking all the necessary spaces for each customer to store his liked colors.

It is important to know that in the array we store only the color and type values of every customer; we do not save the amount of colors a customer wants.

The parser looks for customers who want more than 1 Matte color and for cases where all the customers want glossy colors.

When the color type values are saved, they are stored as boolean, assigning **true** for 0 (glossy) and **false** for 1 (matte).

It is important to know that the values are stored in the array according to the client in the X axis, but for colors we simply take the next available place without associating the axis with the color value.

At the end of the parsing we have the array full of the input data and we also know if a customer wants more than 1 matte color or if all customers want glossy colors.

### Step 2. — Determine all valid combinations

After we have parsed the input file, we have to get the list of all the combinations that meet the restrictions, in order to do that we have to evaluate one by one all the possible combinations with the input data.

In order to find all the possible combinations, we followed the next steps:
  - We iterate the color options for the first customer taking the first value for that customer which is also the very first value in the array.
  - Once we have a value selected, we iterate all the array taking as base the customer axis so we can take a value for every customer.
  - Then we iterate the las customer in the color axis. When we are done, we move to the next value in the color axis for the previous customer.
  - We evaluate every combination to find if is valid. If it is, we create an object containing the valid list along with the amount of matte colors it has. Then we add the object to an array that contains all the valid combinations. 
  - We follow that pattern until we find all the valid combinations.

At this point we have a list of all the valid combinations, if the list is empty means that we don't have any valid combination and the result is IMPOSSIBLE, if it is not empty then we have to select the best combinations (the one with the less amount of matte colors)

#### Step 3 — Determine the best combination

In this step we iterate the list with the objects containing all the valid combinations (with its amount of matte colors). The objective is to find the combination with the lowest amount of matte colors, it is possible because we have that value for every combination. At the end we have the best combination.

#### Step 4 — Extract the final colors and types

Once we have the list with the best combinations, we need to ensure the order and type for every color in the list. We do this by iterating the list, extracting every color and type and add it to a HashMap. The HashMap is used to store the color as the key and the type as the value to ensure the order and also that we don't have repeated colors.

#### Step 5 — Complete absent colors
At this point we have a HashMap with all the colors and types that satisfy all the customers, but we need to ensure that all colors are present so we iterate the HashMap using the key and fill up the absent colors with type glossy (since it's the cheapest). When we are done the final list is ready.

#### Step 6 — Construct the solution string from the HashMap

In this step we simply iterate the HashMap to get all the colors and types and construct the final string for the case. When have it we add the string result to a HashMap that has the case number as the key and the string as the value.

#### Step 7 — Complete all cases

To complete all the cases, we repeat the steps from 2 to 6 until all the cases are done. When they are our HashMap has all the results for the cases and we can display the final results.

### Display the final result

Once we have all the results, we iterate the final HashMap using the key value as the case number and the value as the string result. After every line is displayed we have completed the task.
