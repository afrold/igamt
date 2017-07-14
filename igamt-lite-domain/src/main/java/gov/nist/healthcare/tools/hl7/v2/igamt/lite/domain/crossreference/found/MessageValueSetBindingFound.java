package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;

public class MessageValueSetBindingFound {

  private MessageFound messageFound;
  private ValueSetBinding binding;

  public MessageFound getMessageFound() {
    return messageFound;
  }

  public void setMessageFound(MessageFound messageFound) {
    this.messageFound = messageFound;
  }

  public ValueSetBinding getBinding() {
    return binding;
  }

  public void setBinding(ValueSetBinding binding) {
    this.binding = binding;
  }


}
