import numpy as np
import tensorflow as tf


def create_adversarial_pattern(input_image, input_label, model):
    loss_object = tf.keras.losses.CategoricalCrossentropy()
    with tf.GradientTape() as tape:
        tape.watch(input_image)
        prediction = model(input_image)
        loss = loss_object(input_label, prediction)
    gradient = tape.gradient(loss, input_image)
    signed_grad = tf.sign(gradient)
    return signed_grad


def adversarial_training(model, x_train, y_train, epochs=10, epsilon=0.01):
    for epoch in range(epochs):
        for x_batch, y_batch in tf.data.Dataset.from_tensor_slices((x_train, y_train)).batch(32):
            with tf.GradientTape() as tape:
                # 生成对抗样本
                perturbations = create_adversarial_pattern(x_batch, y_batch, model)
                adversarial_x_batch = x_batch + epsilon * perturbations

                # 在原始样本和对抗样本上训练
                predictions = model(tf.concat([x_batch, adversarial_x_batch], axis=0))
                loss = tf.keras.losses.categorical_crossentropy(
                    tf.concat([y_batch, y_batch], axis=0), predictions)

            gradients = tape.gradient(loss, model.trainable_variables)
            optimizer.apply_gradients(zip(gradients, model.trainable_variables))

        print(f"Epoch {epoch + 1}/{epochs} completed")

# 注意：这只是一个简化的示例，实际使用时需要完整的模型定义和数据准备