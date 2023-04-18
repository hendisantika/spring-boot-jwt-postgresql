# spring-boot-jwt-postgresql

## Generate Public and Private Key
Generate an RSA keypair
```
$ openssl genrsa -out keypair.pem 2048
```

Generate public key
```
$ openssl rsa -in keypair.pem -pubout -out public.txt
```

Generate private key
```
$ openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.txt
```

Sample public key
```
-----BEGIN PUBLIC KEY-----
MIIBIjA...xxxxxx....QAB
-----END PUBLIC KEY-----
```

Sample private key
```
-----BEGIN PRIVATE KEY-----
MII....xxxxx.....C/o/q6k
-----END PRIVATE KEY-----
```