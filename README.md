# componente-firma-electronica

El "Applet de Firma Electrónica Avanzada" es un componente integrable a aplicaciones web, que recibe de la misma una colección de documentos (PDF, XML, Hash) y realice la firma electrónica avanzada de estos, utilizando un token de seguridad provisto por el usuario como elemento de firma.
Entre las ventajas principales de la firma electrónica avanzada se encuentran:

Autenticación: la firma electrónica es equivalente a la firma física del documento, autenticando la identidad de quién firma el documento.
Integridad: asegura que el contenido original del documento no ha sido alterado.
Seguridad y confiabilidad: Proporciona el máximo grado de seguridad y confidencialidad en Internet
La interacción se realiza a través de dos WebServices SOAP estándar que el Applet consume:

Un primer servicio con el que el Applet obtiene la colección de documentos a firmar.
Un segundo servicio para que el Applet entregue nuevamente a la aplicación los documentos ya firmados por el usuario.
Ambos servicios se autentican con un identificador de transacción alfanumérico, generado aleatoriamente por la aplicación y deberá ser obtenido por el Applet como un parámetro de carga.

Para firmar los documentos, el APPLET podrá utilizar los certificados almacenados en los siguientes repositorios:

Tokens USB o Smartcards conectadas y con drivers instalados (Firma tipo PKCS#11).
En el almacén de certificados de los navegadores Web (Firma Tipo Navegador Web)
Archivos de tipo P12 o PFX que el usuario manualmente seleccionara (Firma tipo P12).
A nivel de estándares de firma, soporta la firma de documentos PDF, con el estándar PDFSignature, XML con los estándares XMLDSig y XADES, y el cifrado directo de hashes utilizando la clave privada seleccionada por el usuario, soportando al menos SHA-1 y SHA-256.

Información de la solución:

https://www.gub.uy/agencia-gobierno-electronico-sociedad-informacion-conocimiento/sites/agencia-gobierno-electronico-sociedad-informacion-conocimiento/files/documentos/publicaciones/Informaci%C3%B3n_applet%20firma%20electronica%20avanzada.pdf
