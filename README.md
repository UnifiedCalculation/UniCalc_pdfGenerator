# UniCalc_pdfGenerator
![Heroku](https://heroku-badge.herokuapp.com/?app=unicalc-pdfgenerator&root=swagger-ui.html)

Microservice for generating PDFs 
##### !Important always send a correct URL for the logo of the company! 
(Example: company.logo: https://www.freelogodesign.org/Content/img/logo-samples/flooop.png)

Swagger for testing purposes:
https://unicalc-pdfgenerator.herokuapp.com/swagger-ui.html

### - POST-Request to https://unicalc-pdfgenerator.herokuapp.com/toPdf/offer for generating a offer-PDF

### - POST-Request to https://unicalc-pdfgenerator.herokuapp.com/toPdf/invoice for generating invoice-PDF

###### Both expect this JSON
```json
{
  "discount": 0,
  "entries": [
    {
      "articles": [
        {
          "amount": 0,
          "description": "string",
          "discount": 0,
          "name": "string",
          "number": 0,
          "price": 0,
          "unit": "string"
        }
      ],
      "title": "string"
    }
  ],
  "projectInformation": {
    "bankInformation": {
      "additionalInformation": "string",
      "alternativeProcedure": [
        "string"
      ],
      "invoiceInformation": "string",
      "paymentReference": "string"
    },
    "company": {
      "account": "string",
      "address": "string",
      "city": "string",
      "contactPerson": "string",
      "land": "string",
      "logo": "string",
      "mail": "string",
      "name": "string",
      "phone": "string",
      "url": "string",
      "zip": "string"
    },
    "customer": {
      "address": "string",
      "city": "string",
      "companyName": "string",
      "department": "string",
      "land": "string",
      "name": "string",
      "zip": "string"
    }
  },
  "title": "string"
}
```
  
