# CHM136 Virtual Lab Project

A Java Swing app where CHM136 students prepare for and walk through their labs virtually before the lab.
Students see their pre-lab modules on a dashboard, read each module's overview, answer the
pre-procedure questions interactively, and then move on to a step-by-step simulation of the
lab. The pilot module is **Pre-lab 3: Extraction Lab**: students act as QA at "Lash Miller Inc."
and purify a mislabeled API batch (aspirin or Ponstel contaminated with vanillin) using
liquid-liquid extraction.

## User stories

1. **Dashboard (modules & export):** As a student, I want a dashboard that shows my pre-lab
   modules and my progress on each, lets me open a module, and lets me export a completed
   module as a PDF/CSV for submission, so I can see what's left and hand in my work.
2. **Data storage (JSON & PubChem API):** As a student, I want my progress saved to a JSON file
   and chemical data loaded from the PubChem API, so I can pick up where I left off and work
   with accurate information about each compound.
3. **Procedure scene progression (bench setup and prev & next scenes):** As a student, I want to 
   move through the procedure one scene at a time with displayed detailed steps and set up my bench 
   with the right equipment for each step, so I follow the lab in the correct order.
4. **Procedure equipment functionality (combination logic):** As a student, I want to use the
   lab equipment and combine items, like adding solvents and reagents or transferring layers
   between containers, and see realistic results, so I can practice the techniques before the
   real lab.
5. **Lab Comprehension (pacing, mid procedure quizzing, note taking):** As a student, I want feedback on 
   each step and answer and take notes for the procedure, so I can fix my mistakes and understand the chemistry.
 

## Running the project

Requires Java 11+ and Maven.

```bash
mvn compile
java -cp target/classes app.LabApp   # launch the app
mvn test                             # run the tests
mvn checkstyle:check                 # Google style check (also runs on every PR)
```

## Project structure

The code follows the team's Clean Architecture UML class diagram.

<!-- TODO -->
