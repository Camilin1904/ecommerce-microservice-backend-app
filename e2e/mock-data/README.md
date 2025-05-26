# Mock Data

Place your mock data JSON files in this directory. These files can be used as iteration data for your Newman tests.

Example format:
```json
[
  {
    "id": "1",
    "name": "Test Product 1",
    "price": 99.99,
    "description": "This is a test product"
  },
  {
    "id": "2",
    "name": "Test Product 2",
    "price": 199.99,
    "description": "This is another test product"
  }
]
```

You can reference these values in your Postman collection using the following syntax:
```
{{id}}
{{name}}
{{price}}
{{description}}
```
