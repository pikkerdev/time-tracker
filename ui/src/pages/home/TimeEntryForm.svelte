<script lang="ts">
  import Form from 'src/forms/Form.svelte'
  import SelectField from 'src/forms/SelectField.svelte'
  import {t} from 'i18n'
  import FormField from 'src/forms/FormField.svelte'
  import NumberField from 'src/forms/NumberField.svelte'
  import Button from 'src/components/Button.svelte'



</script>

  <Form>
    <SelectField label={t.projects.project} options=""/>
    <FormField label={t.timeEntries.date}/>
    <NumberField label={t.timeEntries.hours}/>
    <FormField label={t.timeEntries.storyId}/>
    <FormField label={t.timeEntries.description}/>
    <NumberField label={t.projects.hourlyRate}/>
    <Button type="submit" label={t.general.save} class="primary"/>
  </Form>

