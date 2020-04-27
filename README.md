# UniCalc_pdfGenerator
Microservice for generating PDFs

#### - POST-Request to localhost:8080/toPdf/offer for generating a offer-PDF

#### - POST-Request to localhost:8080/toPdf/invoice for generating invoice-PDF

###### Both expect this JSON
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
    "company": {  
      "address": "string",  
      "city": "string",  
      "contactPerson": "string",  
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
  
