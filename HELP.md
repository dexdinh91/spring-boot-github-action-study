# Install uv
powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"

# Install Specify CLI
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v0.11.6

# Init existing project
specify init --here

# Establish project principles (using Github Copilot, not cmd!)
Use the /speckit.constitution agent to create your project's governing principles and development guidelines that will guide all subsequent development.

I.e: /speckit.constitution Create principles focused on code quality, testing standards, user experience consistency, and performance requirements
=> output: 
\.specify\memory\constitution.md

# Create the spec
Use the /speckit.specify agent to describe what you want to build. Focus on the what and why, not the tech stack.

I.e: /speckit.specify I want to build an additional API which has format /users/phone=xxx to get the user by phone number, the output should be User domain,
in the repository, you can create a dummy return.
=> output:
- .specify/feature.json -> state file that Spec Kit uses to follow the current feature which is processing
- \specs\001-get-user-by-phone\spec.md -> functional specification document for a feature
- \specs\001-get-user-by-phone\checklists\requirements.md -> quality checklist to evaluate spec.md is good enough?
And no code was implemented (you can enrich spec.md later)

# Remove ambiguity
Use the /speckit.clarify agent to review the generated spec.md, finds ambiguous or missing requirements, and asks you targeted questions before moving on to implementation planning

I.e: /speckit.clarify review \specs\001-get-user-by-phone\spec.md and make updated on this file
=> output: 
spec.md is updated


# Create a technical implementation plan
Use the /speckit.plan command to provide your tech stack and architecture choices.



# Break down into tasks
Use /speckit.tasks to create an actionable task list from your implementation plan.

# Execute implementation
Use /speckit.implement to execute all tasks and build your feature according to the plan.


