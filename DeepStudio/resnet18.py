import tensorflow
def make_resnet18_model(input_width,input_height,input_channel):
    inputs=tensorflow.keras.layers.Input(shape=[input_width,input_height,input_channel])
    conv2=tensorflow.keras.layers.Conv2D(filters=output_channel,strides=1,kernel_size=3)
    conv1=tensorflow.keras.layers.Conv2D(filters=input_channel,strides=1,kernel_size=3)
    _output_inputs=inputs
    _output_conv1=conv1(_output_inputs)
    _output_conv2=conv2(_output_conv1)
    _model=tensorflow.keras.Model(outputs=_output_conv2,inputs=inputs)
    return _model
def _test():
    _model=make_resnet18_model(64,64,128)
    _model.summary()
    
if __name__=='__main__':
    _test()
