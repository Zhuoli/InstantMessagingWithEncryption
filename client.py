import os
l1='openssl genrsa -out private_key.pem 2048'
l2='openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt'
l3='openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der'
l4='mv *.der ./client/'
l5='rm *.pem'
os.system(l1)
os.system(l2)
os.system(l3)
os.system(l4)
os.system(l5)

os.system('java client.Client')
