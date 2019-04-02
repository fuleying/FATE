# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: inference_service.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='inference_service.proto',
  package='com.webank.ai.fate.api.serving',
  syntax='proto3',
  serialized_pb=_b('\n\x17inference_service.proto\x12\x1e\x63om.webank.ai.fate.api.serving\"m\n\rFederatedMeta\x12\x0f\n\x07sceneId\x18\x01 \x01(\t\x12\x11\n\tmyPartyId\x18\x02 \x01(\t\x12\x16\n\x0epartnerPartyId\x18\x03 \x01(\t\x12\x0e\n\x06myRole\x18\x04 \x01(\t\x12\x10\n\x08\x63ommitId\x18\x05 \x01(\t\"]\n\x10InferenceRequest\x12;\n\x04meta\x18\x01 \x01(\x0b\x32-.com.webank.ai.fate.api.serving.FederatedMeta\x12\x0c\n\x04\x64\x61ta\x18\x02 \x01(\x0c\"\x92\x01\n\x11InferenceResponse\x12;\n\x04meta\x18\x01 \x01(\x0b\x32-.com.webank.ai.fate.api.serving.FederatedMeta\x12\x12\n\nstatusCode\x18\x02 \x01(\x05\x12\x0f\n\x07message\x18\x03 \x01(\t\x12\r\n\x05\x65rror\x18\x04 \x01(\t\x12\x0c\n\x04\x64\x61ta\x18\x05 \x01(\x0c\x32\x82\x01\n\x10InferenceService\x12n\n\x07predict\x12\x30.com.webank.ai.fate.api.serving.InferenceRequest\x1a\x31.com.webank.ai.fate.api.serving.InferenceResponseB\x17\x42\x15InferenceServiceProtob\x06proto3')
)




_FEDERATEDMETA = _descriptor.Descriptor(
  name='FederatedMeta',
  full_name='com.webank.ai.fate.api.serving.FederatedMeta',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='sceneId', full_name='com.webank.ai.fate.api.serving.FederatedMeta.sceneId', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='myPartyId', full_name='com.webank.ai.fate.api.serving.FederatedMeta.myPartyId', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='partnerPartyId', full_name='com.webank.ai.fate.api.serving.FederatedMeta.partnerPartyId', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='myRole', full_name='com.webank.ai.fate.api.serving.FederatedMeta.myRole', index=3,
      number=4, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='commitId', full_name='com.webank.ai.fate.api.serving.FederatedMeta.commitId', index=4,
      number=5, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=59,
  serialized_end=168,
)


_INFERENCEREQUEST = _descriptor.Descriptor(
  name='InferenceRequest',
  full_name='com.webank.ai.fate.api.serving.InferenceRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='meta', full_name='com.webank.ai.fate.api.serving.InferenceRequest.meta', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='data', full_name='com.webank.ai.fate.api.serving.InferenceRequest.data', index=1,
      number=2, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=170,
  serialized_end=263,
)


_INFERENCERESPONSE = _descriptor.Descriptor(
  name='InferenceResponse',
  full_name='com.webank.ai.fate.api.serving.InferenceResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='meta', full_name='com.webank.ai.fate.api.serving.InferenceResponse.meta', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='statusCode', full_name='com.webank.ai.fate.api.serving.InferenceResponse.statusCode', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='message', full_name='com.webank.ai.fate.api.serving.InferenceResponse.message', index=2,
      number=3, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='error', full_name='com.webank.ai.fate.api.serving.InferenceResponse.error', index=3,
      number=4, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='data', full_name='com.webank.ai.fate.api.serving.InferenceResponse.data', index=4,
      number=5, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=266,
  serialized_end=412,
)

_INFERENCEREQUEST.fields_by_name['meta'].message_type = _FEDERATEDMETA
_INFERENCERESPONSE.fields_by_name['meta'].message_type = _FEDERATEDMETA
DESCRIPTOR.message_types_by_name['FederatedMeta'] = _FEDERATEDMETA
DESCRIPTOR.message_types_by_name['InferenceRequest'] = _INFERENCEREQUEST
DESCRIPTOR.message_types_by_name['InferenceResponse'] = _INFERENCERESPONSE
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

FederatedMeta = _reflection.GeneratedProtocolMessageType('FederatedMeta', (_message.Message,), dict(
  DESCRIPTOR = _FEDERATEDMETA,
  __module__ = 'inference_service_pb2'
  # @@protoc_insertion_point(class_scope:com.webank.ai.fate.api.serving.FederatedMeta)
  ))
_sym_db.RegisterMessage(FederatedMeta)

InferenceRequest = _reflection.GeneratedProtocolMessageType('InferenceRequest', (_message.Message,), dict(
  DESCRIPTOR = _INFERENCEREQUEST,
  __module__ = 'inference_service_pb2'
  # @@protoc_insertion_point(class_scope:com.webank.ai.fate.api.serving.InferenceRequest)
  ))
_sym_db.RegisterMessage(InferenceRequest)

InferenceResponse = _reflection.GeneratedProtocolMessageType('InferenceResponse', (_message.Message,), dict(
  DESCRIPTOR = _INFERENCERESPONSE,
  __module__ = 'inference_service_pb2'
  # @@protoc_insertion_point(class_scope:com.webank.ai.fate.api.serving.InferenceResponse)
  ))
_sym_db.RegisterMessage(InferenceResponse)


DESCRIPTOR.has_options = True
DESCRIPTOR._options = _descriptor._ParseOptions(descriptor_pb2.FileOptions(), _b('B\025InferenceServiceProto'))

_INFERENCESERVICE = _descriptor.ServiceDescriptor(
  name='InferenceService',
  full_name='com.webank.ai.fate.api.serving.InferenceService',
  file=DESCRIPTOR,
  index=0,
  options=None,
  serialized_start=415,
  serialized_end=545,
  methods=[
  _descriptor.MethodDescriptor(
    name='predict',
    full_name='com.webank.ai.fate.api.serving.InferenceService.predict',
    index=0,
    containing_service=None,
    input_type=_INFERENCEREQUEST,
    output_type=_INFERENCERESPONSE,
    options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_INFERENCESERVICE)

DESCRIPTOR.services_by_name['InferenceService'] = _INFERENCESERVICE

# @@protoc_insertion_point(module_scope)
