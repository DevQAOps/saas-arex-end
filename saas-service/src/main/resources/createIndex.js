db.getCollection("Tenant").createIndex({ "email": 1 });
db.getCollection("Tenant").createIndex({ "tenantCode": 1 });
db.getCollection("Verification").createIndex({ "email": 1 });
db.getCollection("Verification").createIndex({ "tenantCode": 1 });
