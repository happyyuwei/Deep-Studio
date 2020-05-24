import tensorflow
def make_residual_model(input_width,input_height,input_channel):
    relu2=tensorflow.keras.layers.ReLU()
    add=tensorflow.keras.layers.Add()
    relu3=tensorflow.keras.layers.ReLU()
    relu1=tensorflow.keras.layers.ReLU()
    inputs=tensorflow.keras.layers.Input(shape=[input_width,input_height,input_channel])
    conv2=tensorflow.keras.layers.Conv2D(padding="same",filters=input_channel,strides=1,kernel_size=3)
    conv1=tensorflow.keras.layers.Conv2D(padding="same",filters=input_channel,strides=1,kernel_size=3)
    _output_inputs=inputs
    _output_conv1=conv1(_output_inputs)
    _output_relu1=relu1(_output_conv1)
    _output_conv2=conv2(_output_relu1)
    _output_relu2=relu2(_output_conv2)
    _output_add=add([_output_relu2,_output_inputs])
    _output_relu3=relu3(_output_add)
    _model=tensorflow.keras.Model(outputs=_output_relu3,inputs=inputs)
    return _model
def _test():
    _model=make_residual_model(64,64,64)
    _model.summary()
    
if __name__=='__main__':
    _test()
