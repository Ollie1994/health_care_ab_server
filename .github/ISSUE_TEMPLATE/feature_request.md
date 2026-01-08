---
name: Feature request
about: Suggest an idea for this project
title: ''
labels: ''
assignees: ''

---

### Things of interest for the person working with this issue:
- Example - In order to test endpoint patch booking you need these logins (Ollie94, Password123!!!)
- Example -  Its just a very basic implementation and still need work, but i will create a different issue for that in the future

### Blocked by:
Example - Blocked by: Issue #3

# Implement:
Example - Short title of what is being implemented
## Description:
Example - Description of what is being implemented

# Functionality
## Scenarios that are handled by error or validation handling:
- [ ] Example - A User cant register with an invalid email format
- [ ] Example - A User cant retrieve a booking that isn't his or hers
## Endpoints to be implemented:
- [ ] Example - Get Mapping for a user
- [ ] Example - Post Mapping for registering a user
- [ ] Example - Get Mapping for rentals based on category inputted
## Technical requirements:
- [ ] All endpoints has to return valid HTTP-status codes. (201 created, 400 bad request)
- [ ] Basic validation has to be implemented
- [ ] Set createdAt and updatedAt automatically or in service class (date.now)
- [ ] Validation for null/empty fields and eventually min/max values
- [ ] Error handling at method level
- [ ] Authorization handling on a method level and or config level

# API-structure
- Example - GET /user = Get all users
- Example - GET /rental/availability/:{startDate}/:{endDate} = Get all rentals based on inputted start and end date

# Acceptance criteria
- [ ] All listed endpoints are implemented and tested
- [ ] The API returns JSON response
- [ ] Basic validation has been implemented
- [ ] The code is following our code standard
- [ ] The API has been manually tested

# Technical details
Framework: Spring Boot
Database: MongoDB
Difficulty: Example - Easy
Time estimate: Example - 1-2 days