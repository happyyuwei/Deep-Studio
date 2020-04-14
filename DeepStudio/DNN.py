import tensorflow
def make_DNN_model():
    flatten=tensorflow.keras.layers.Flatten()
    inputs=tensorflow.keras.layers.Input(shape=[28,28])
    dropout=tensorflow.keras.layers.Dropout(rate=0.2)
    layer1=tensorflow.keras.layers.Dense(units=128,activation="relu")
    layer2=tensorflow.keras.layers.Dense(units=10,activation="softmax")
    _output_inputs=inputs
    _output_flatten=flatten(_output_inputs)
    _output_layer1=layer1(_output_flatten)
    _output_dropout=dropout(_output_layer1)
    _output_layer2=layer2(_output_dropout)
    _model=tensorflow.keras.Model(outputs=_output_layer2,inputs=inputs)
    return _model
def _test():
    _model=make_DNN_model()
    _model.summary()
    
if __name__=='__main__':
    _test()
