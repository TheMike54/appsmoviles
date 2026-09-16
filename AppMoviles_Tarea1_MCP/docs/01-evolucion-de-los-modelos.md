# 1. Evolución de los modelos

## ¿Qué es un modelo de lenguaje (LM)?

Un modelo de lenguaje es un modelo que asigna probabilidades a secuencias de palabras, o sea
que, dado un texto, calcula cuál es la siguiente palabra más probable. A esos fragmentos se les
llama **tokens**; pueden ser palabras completas o pedazos de palabra, porque no hay una
definición exacta de dónde se corta y varía según el modelo. Todo lo que "escribe" un modelo
sale de repetir ese proceso: predecir un token, agregarlo al texto y volver a predecir
(Jurafsky & Martin, 2025).

Este tema me recordó mucho a un video de Nate Gentile (s.f.-a), que fue con el que hace algunos
años entendí por primera vez cómo funcionaba esto. En ese entonces él hablaba de ChatGPT, y para
explicarlo propuso una analogía que se me quedó muy grabada: la del **teclado del teléfono**.

Cuando escribimos un mensaje, arriba del teclado sale una ventanita que trata de adivinar cuál
va a ser nuestra siguiente palabra. Eso es, en esencia, un modelo de lenguaje: tiene un conjunto
de palabras y calcula por probabilidad cuál sigue. Si escribo "hola", lo más probable es que
después venga "cómo estás".

Qué tan bien le atine depende del texto con el que se entrenó. Por eso el teclado del celular
termina sugiriendo las palabras que uno usa seguido.

Los primeros modelos de lenguaje eran estadísticos, como los **n-gramas**, que solo miraban
las últimas 2 o 3 palabras para adivinar la siguiente. Después llegaron las redes neuronales
recurrentes (RNN y LSTM), que podían considerar un contexto más largo, pero procesaban el texto
palabra por palabra y les costaba recordar lo que venía muy atrás.

## De LM a LLM

El cambio grande fue la arquitectura **Transformer** (Vaswani et al., 2017). En lugar de leer
palabra por palabra en orden, usa un mecanismo llamado **atención**, que relaciona todas las
palabras del texto entre sí y le da más peso a las que sí importan para la predicción. De ahí
sale lo que nosotros llamamos "contexto".

Con un ejemplo: en *"el perro que estaba en el parque estaba cansado"*, para saber quién está
cansado hay que relacionar "cansado" con "perro" y no con "parque", aunque estén lejos. Una RNN
tenía que arrastrar esa información palabra por palabra y se le iba perdiendo; el Transformer
las relaciona directamente.

Hay dos cosas que me ayudaron a entender bien la diferencia entre un LM y un LLM:

- **Con el Transformer no es que el modelo entienda**, sino que al relacionar las palabras puede
  generar el contexto, y con ese contexto toma en cuenta todo el texto anterior para predecir
  mejor la palabra que sigue. No hay comprensión como la de una persona: hay relaciones con
  pesos.
- **La gran diferencia entre un LM y un LLM son los parámetros.** Como dice su nombre, *Large*
  Language Model: un LLM tiene una cantidad enorme de parámetros. Justamente por eso no lo
  podemos correr en nuestra computadora, porque son tantos datos que no caben. El Transformer no
  es lo que los separa (un modelo pequeño también puede usarlo); lo que hizo fue volver
  *posible* esa escala, porque al no procesar en fila el entrenamiento se puede repartir entre
  muchas GPUs a la vez.

Un **modelo de lenguaje grande (LLM)** es básicamente un modelo de lenguaje basado en
Transformer, pero escalado en tres ejes: número de parámetros, cantidad de datos de
entrenamiento y cómputo usado para entrenarlo. Kaplan et al. (2020) mostraron que el error del
modelo baja de forma predecible al crecer esos tres factores (las llamadas *leyes de
escalamiento*). GPT-3, con 175 mil millones de parámetros, fue el ejemplo que popularizó la
idea: sin reentrenarlo, podía resolver tareas nuevas solo con ver unos cuantos ejemplos en el
prompt (Brown et al., 2020).

Entonces, un LLM es un modelo de lenguaje que sigue haciendo lo mismo (predecir la siguiente
palabra), pero con dos diferencias: usa atención, así que puede aprovechar todo el contexto
anterior, y está entrenado a una escala enorme, lo que hace que sus respuestas sean mucho
mejores.

Y ahí está la contra, que conviene tener presente para el resto de la tarea: entre más
parámetros, mejores respuestas, pero también hace falta hardware mucho más potente para guardar
y ejecutar el modelo. Uno de miles de millones de parámetros no corre en una laptop normal:
necesita servidores con GPUs dedicadas. Por eso los modelos grandes se ejecutan en los centros
de datos del proveedor y nosotros solo les mandamos texto, que es una de las razones del
aislamiento que se explica en el punto 2.

| Etapa | Idea principal | Limitación |
|---|---|---|
| n-gramas | Contar qué palabras suelen ir juntas | Contexto de muy pocas palabras |
| RNN / LSTM | Red neuronal con "memoria" secuencial | Lentas de entrenar, olvidan contexto largo |
| Transformer (2017) | Atención sobre todo el texto a la vez | Necesita mucho cómputo y datos |
| LLM (2020 en adelante) | Transformer escalado a miles de millones de parámetros | Solo predice texto; no razona paso a paso por sí mismo de forma confiable |

## Modelos con razonamiento explícito

Los modelos con razonamiento explícito (por ejemplo OpenAI o1 o DeepSeek-R1) son LLMs que,
antes de dar la respuesta final, generan una cadena de pasos intermedios: plantean el problema,
prueban caminos, se corrigen y verifican. A eso se le suele llamar *cadena de pensamiento*
(chain of thought).

Lo importante, y lo que hay que dejar claro, es que **esta capacidad no aparece sola por hacer
el modelo más grande**. Viene de dos cosas:

1. **Técnicas de entrenamiento.** Primero se vio que pedirle al modelo "piensa paso a paso"
   mejoraba sus resultados en problemas de lógica y matemáticas (Wei et al., 2022). Pero los
   modelos de razonamiento actuales van más allá: se entrenan con **aprendizaje por refuerzo**,
   premiando al modelo cuando llega a una respuesta correcta que se puede verificar (un
   resultado matemático, un código que pasa pruebas). DeepSeek-AI (2025) reportó que con este
   entrenamiento surgieron comportamientos como la autorreflexión y la verificación de sus
   propios pasos, sin que nadie le diera ejemplos humanos de cómo razonar.
2. **Cómputo adicional en el momento de la inferencia** (*test-time compute*). El modelo
   "piensa más tiempo" antes de contestar, generando más tokens de razonamiento. OpenAI (2024)
   reportó que el desempeño de o1 mejora tanto con más aprendizaje por refuerzo como con más
   tiempo de pensamiento al responder. Snell et al. (2024) encontraron que, bien distribuido,
   ese cómputo extra al responder puede superar a un modelo 14 veces más grande.

En resumen: un LLM más grande sabe más cosas, pero un modelo de razonamiento es uno al que
además se le **entrenó** para razonar y al que se le **da tiempo** para hacerlo. Eso explica
también por qué estos modelos consumen más tokens y salen más caros: parte del trabajo es el
texto de razonamiento que genera antes de contestar.

La primera vez que probé un modelo de razonamiento fue con **ChatGPT o1**. En ese entonces yo
tenía la suscripción de ChatGPT, y cuando salió ese modelo me pareció bastante interesante:
cuando le escribía algo y lo seleccionaba, el chat literalmente se ponía a pensar antes de
contestarme. Obviamente tardaba más en darme la respuesta, pero las respuestas que me daba eran
considerablemente mejores que las de los modelos que había usado antes.

## Relación con esta tarea

Aquí está el punto que conecta con el resto: aunque un modelo razone muy bien, sigue siendo un
modelo que recibe texto y devuelve texto. Por bueno que sea razonando, **no puede abrir mis
archivos, ni entrar a mi correo, ni ver mi proyecto**; solo trabaja con lo que le pego en la
conversación.

Eso es justo lo que resuelve MCP, que es el tema de esta tarea: darle herramientas al modelo
para que pueda usar cosas que están en mi computadora o en servicios que yo autorice. El
problema completo se explica en el siguiente punto:
[El problema del aislamiento](02-problema-del-aislamiento.md).

## Fuentes

- Brown, T. B., Mann, B., Ryder, N., Subbiah, M., Kaplan, J., Dhariwal, P., et al. (2020). *Language models are few-shot learners*. arXiv. https://arxiv.org/abs/2005.14165
- DeepSeek-AI. (2025). DeepSeek-R1: Incentivizing reasoning capability in LLMs via reinforcement learning. *Nature, 645*, 633–638. https://arxiv.org/abs/2501.12948
- Jurafsky, D., & Martin, J. H. (2025). *Speech and language processing* (3rd ed. draft). Stanford University. https://web.stanford.edu/~jurafsky/slp3/
- Kaplan, J., McCandlish, S., Henighan, T., Brown, T. B., Chess, B., Child, R., Gray, S., Radford, A., Wu, J., & Amodei, D. (2020). *Scaling laws for neural language models*. arXiv. https://arxiv.org/abs/2001.08361
- Nate Gentile [@NateGentile7]. (s.f.-a). *¿Cómo funciona ChatGPT? La revolución de la Inteligencia Artificial* [Video]. YouTube. https://www.youtube.com/watch?v=FdZ8LKiJBhQ
- OpenAI. (2024, 12 de septiembre). *Learning to reason with LLMs*. https://openai.com/index/learning-to-reason-with-llms/
- Snell, C., Lee, J., Xu, K., & Kumar, A. (2024). *Scaling LLM test-time compute optimally can be more effective than scaling model parameters*. arXiv. https://arxiv.org/abs/2408.03314
- Vaswani, A., Shazeer, N., Parmar, N., Uszkoreit, J., Jones, L., Gomez, A. N., Kaiser, Ł., & Polosukhin, I. (2017). Attention is all you need. *Advances in Neural Information Processing Systems, 30*. https://arxiv.org/abs/1706.03762
- Wei, J., Wang, X., Schuurmans, D., Bosma, M., Ichter, B., Xia, F., Chi, E., Le, Q., & Zhou, D. (2022). *Chain-of-thought prompting elicits reasoning in large language models*. arXiv. https://arxiv.org/abs/2201.11903
