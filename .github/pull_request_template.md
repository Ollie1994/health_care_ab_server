# Pull Request Template


## Notes & Questions for the Reviewer
- Are there any fields that should be nullable / optional?
- Is the validation level appropriate?
- Any logic that should be refactored or moved?
- General feedback or concerns?


## What has been changed?
Briefly describe what has been implemented or modified.


## Why did I make these changes?
Explain **why** these changes were necessary.


### Endpoints
| Method | Endpoint |
|------|---------|
| POST | /api/... |
| GET  | /api/... |


## How can others test my code?
1. Check out the branch
2. Start the server
3. Test all endpoints using Postman
4. Test negative scenarios (invalid input, missing inputs, etc.)


## Checklist before submitting PR
Check each box by adding an `x` → `[x]`


- [ ] I have tested my code locally
- [ ] My code follows the project coding standards
- [ ] I have commented complex parts of the code where needed
- [ ] Documentation has been updated (if needed)
- [ ] No unnecessary logs or debug code remain


## Screenshots (if applicable)
## Reminder
- Be open and respectful to feedback
- Respond to review comments promptly
- Mistakes are okay — improvement is the goal


# For the reviewer:


## Checklist before merging the PR
Check each box by adding an `x` → `[x]`


- [ ] I have tested the code locally
- [ ] I have reviewed all changes with another member of the team
- [ ] The code follows the project coding standards
- [ ] I have tested all positive and negative scenarios
- [ ] Error handling uses the correct exceptions
- [ ] Responses return the correct HTTP status code